package host.bloom.ab.common.managers;

import ch.jalu.configme.SettingsManager;
import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.config.ConfigKeys;
import host.bloom.ab.common.config.enums.Messages;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CounterManager {

    private final AbstractPlugin plugin;
    private final Map<Long, Integer> connectionCounts = new HashMap<>();
    private final Set<UUID> seers = new HashSet<>();

    private long lastTriggerTime = 0;
    private boolean forceTrigger = false;
    private boolean activated = false;
    private int forcedTriggerDuration;

    private final SettingsManager config = ConfigManager.getConfig();

    public CounterManager(AbstractPlugin plugin) {
        this.plugin = plugin;

        // Initialize the schedule to track connections
        plugin.getScheduler().schedule(() -> {
            resetConnectionCounts();
            logCurrentCount();
        }, 1, 1, TimeUnit.SECONDS);
    }

    private CompletableFuture<Boolean> makeHttpRequest(boolean isStop) {
        return CompletableFuture.supplyAsync(() -> {

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(this.config.getProperty(ConfigKeys.locations).getEndpoint()).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("User-Agent", "BloomAB");
                conn.setDoOutput(true);

                String data;
                if (isStop) {
                    data = "STOP=1&ip_address=" + this.config.getProperty(ConfigKeys.api_endpoint) + "&secret_key=" + this.config.getProperty(ConfigKeys.api_secret);
                } else {
                    data = "triggerTime=" + this.forcedTriggerDuration + "&ip_address=" +
                            this.config.getProperty(ConfigKeys.api_endpoint) + "&block_new_joins=" +
                            this.config.getProperty(ConfigKeys.block_new_joins).getRaw() + "&secret_key=" +
                            this.config.getProperty(ConfigKeys.api_secret) + "&maxrps=" +
                            this.config.getProperty(ConfigKeys.max_joins_per_second);
                }

                try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                    wr.writeBytes(data);
                    wr.flush();
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    plugin.getABLogger().info("Remote call successful: trigger " + (isStop ? "stopped." : "enabled."));
                    return true;
                } else {
                    plugin.getABLogger().error("Remote call failed (HTTP Error " + responseCode + "), trigger " + (isStop ? "stop" : "start") + " canceled.");
                    return false;
                }

            } catch (IOException exception) {
                this.plugin.getABLogger().error("Unable to make API call:" + exception.getMessage());
                exception.printStackTrace();
                return false;
            } finally {
                if (conn != null) conn.disconnect();
            }
        });
    }

    public void retryStopTrigger() {
        plugin.getScheduler().schedule(() -> makeHttpRequest(true).thenAccept(success -> {
            if (!success) {
                plugin.getABLogger().info("Retry to stop the trigger failed, retrying in 1 minute.");
                retryStopTrigger();
            } else {
                plugin.getABLogger().info("Retry to stop the trigger succeeded.");
            }
        }), 1, TimeUnit.MINUTES);
    }

    public CompletableFuture<Void> setForceTrigger(boolean forceTrigger, int seconds) {
        CompletableFuture<Void> exceptionFuture = new CompletableFuture<>();

        if (seconds > 3600) {
            exceptionFuture.completeExceptionally(new Exception("Trigger duration exceeds the maximum allowed value (3600 seconds)."));
            return exceptionFuture;
        }

        this.lastTriggerTime = System.currentTimeMillis() / 1000;
        this.forcedTriggerDuration = seconds == 0 ? this.config.getProperty(ConfigKeys.trigger_duration) : seconds;

        return makeHttpRequest(!forceTrigger).thenCompose(response -> {
            if (!response) {
                exceptionFuture.completeExceptionally(new Exception("Failed to make the HTTP request."));
                return exceptionFuture;
            }

            this.forceTrigger = forceTrigger;
            if (!forceTrigger) {
                return CompletableFuture.completedFuture(null);
            }

            plugin.getScheduler().schedule(() -> {
                if (this.forceTrigger) {
                    this.forceTrigger = false;
                    makeHttpRequest(true).thenAccept(success -> { // make HTTP request when stopping the trigger
                        if (!success) {
                            plugin.getABLogger().info("Failed to stop the trigger, scheduling retries.");
                            retryStopTrigger();
                        } else {
                            plugin.getABLogger().info("Force trigger has been deactivated.");
                            activated = false;
                        }
                    });
                }
            }, this.forcedTriggerDuration, TimeUnit.SECONDS);
            return CompletableFuture.completedFuture(null);
        });
    }

    public void incrementConnectionCount() {
        long currentTime = System.currentTimeMillis() / 1000;
        int currentCount = connectionCounts.getOrDefault(currentTime, 0);
        connectionCounts.put(currentTime, currentCount + 1);

        if ((currentCount + 1 > this.config.getProperty(ConfigKeys.max_joins_per_second) || forceTrigger) && (currentTime - lastTriggerTime) > 1 && !activated) {
            lastTriggerTime = currentTime;
            activated = true;
            plugin.getABLogger().error("Limit reached! Trigger enabled. JPS: " + (currentCount + 1));
            setForceTrigger(true, this.config.getProperty(ConfigKeys.trigger_duration)).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
        }
    }

    private void resetConnectionCounts() {
        long currentTime = System.currentTimeMillis() / 1000;
        long startTime = currentTime - 10;

        connectionCounts.entrySet().removeIf(entry -> entry.getKey() < startTime);

        int currentCount = connectionCounts.getOrDefault(currentTime, 0);
        connectionCounts.put(currentTime, currentCount);
    }

    private void logCurrentCount() {
        if (forceTrigger) {
            long currentTime = System.currentTimeMillis() / 1000;
            int currentCount = connectionCounts.getOrDefault(currentTime, 0);
            plugin.getABLogger().info("Number of received JPS: " + currentCount);

            if (!this.seers.isEmpty()) {
                this.seers.forEach(uuid -> this.plugin.actionbar(uuid, Messages.action_bar.getMessage("{joins}", String.valueOf(currentCount))));
            }
        }
    }

    public long getRemainingSeconds() {
        if (forceTrigger) {
            long currentTimeMillis = System.currentTimeMillis();
            long elapsedTimeMillis = currentTimeMillis - (lastTriggerTime * 1000L);
            long remainingMillis = (forcedTriggerDuration * 1000L) - elapsedTimeMillis;
            return Math.max(remainingMillis / 1000L, 0);
        }
        return 0;
    }

    public int getCurrentCount(long currentTime) {
        return connectionCounts.getOrDefault(currentTime, 0);
    }

    public boolean isForceTrigger() {
        return forceTrigger;
    }

    public void addSeer(UUID uuid) {
        this.seers.add(uuid);
    }

    public boolean containsSeer(UUID uuid) {
        return this.seers.contains(uuid);
    }

    public void removeSeer(UUID uuid) {
        this.seers.remove(uuid);
    }
}