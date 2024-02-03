package host.bloom.ab.common.managers;

import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.config.Config;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CounterManager {

    private final AbstractPlugin plugin;
    private final Config config;
    private final Map<Long, Integer> connectionCounts = new HashMap<>();

    private long lastTriggerTime = 0;
    private boolean forceTrigger = false;
    private boolean activated = false;
    private int forcedTriggerDuration;

    public CounterManager(AbstractPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        // Initialize the schedule
        plugin.getScheduler().schedule(() -> {
            resetConnectionCounts();
            logCurrentCount();
        }, 1, 1, TimeUnit.SECONDS);
    }

    private CompletableFuture<Boolean> makeHttpRequest(boolean isStop) {
        return CompletableFuture.supplyAsync(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL("https://abapi.lowhosting.org/");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("User-Agent", "BloomAB");
                conn.setDoOutput(true);

                String data;
                if (isStop) {
                    data = "STOP=1&ip_address=" + config.ipAddress + "&secret_key=" + config.secretKey;
                } else {
                    data = "triggerTime=" + this.forcedTriggerDuration + "&ip_address=" + config.ipAddress + "&block_new_joins=" + config.blockNewJoins + "&secret_key=" + config.secretKey + "&maxrps=" + config.maxJoinsPerSecond;
                }

                try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                    wr.writeBytes(data);
                    wr.flush();
                }

                int responseCode = conn.getResponseCode();

                if (responseCode == 200) {
                    plugin.getLogger().info("Remote call successful: trigger " + (isStop ? "stopped." : "enabled."));
                    return true;
                } else {
                    plugin.getLogger().severe("Remote call failed (HTTP Error " + responseCode + "), trigger " + (isStop ? "stop" : "start") + " canceled.");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }

    public void retryStopTrigger() {
        plugin.getScheduler().schedule(() -> makeHttpRequest(true).thenAccept(success -> {
            if (!success) {
                plugin.getLogger().info("Retry to stop the trigger failed, retrying in 1 minute.");
                retryStopTrigger();
            } else {
                plugin.getLogger().info("Retry to stop the trigger succeeded.");
            }
        }), 1, TimeUnit.MINUTES);
    }

    public CompletableFuture<Void> setForceTrigger(boolean forceTrigger, int seconds) {
        CompletableFuture<Void> exceptionFuture = new CompletableFuture<>();

        if ("<yourserverip>".equals(config.ipAddress) || "<yourkey>".equals(config.secretKey)) {
            exceptionFuture.completeExceptionally(new Exception("Plugin configuration is not configured! Please update the 'ip_address' and 'secret_key' values in the config file."));
            return exceptionFuture;
        }

        if (seconds > 3600) {
            exceptionFuture.completeExceptionally(new Exception("Trigger duration exceeds the maximum allowed value (3600 seconds)."));
            return exceptionFuture;
        }

        this.lastTriggerTime = System.currentTimeMillis() / 1000;
        this.forcedTriggerDuration = seconds == 0 ? config.triggerDuration : seconds;

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
                            plugin.getLogger().info("Failed to stop the trigger, scheduling retries.");
                            retryStopTrigger();
                        } else {
                            plugin.getLogger().info("Force trigger has been deactivated.");
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

        if ((currentCount + 1 > config.maxJoinsPerSecond || forceTrigger) && (currentTime - lastTriggerTime) > 1 && !activated) {
            lastTriggerTime = currentTime;
            activated = true;
            plugin.getLogger().severe("Limit reached! Trigger enabled. JPS: " + (currentCount + 1));
            setForceTrigger(true, config.triggerDuration).exceptionally(ex -> {
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
            plugin.getLogger().info("Number of received JPS: " + currentCount);
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

}

