package me.bloomab.managers;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import me.bloomab.BloomAB;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class CounterManager {

    private final Map<Long, Integer> connectionCounts = Maps.newHashMap();
    private final BloomAB bloomAB = BloomAB.getInstance();

    private long lastTriggerTime = 0;

    @Getter
    private boolean forceTrigger = false;

    @Getter
    @Setter
    private int triggerDuration;

    @Getter
    @Setter
    private int maxJoinsPerSecond;

    @Getter
    @Setter
    private String ipAddress;

    @Getter
    @Setter
    private String secretKey;

    @Getter
    @Setter
    private String blockNewJoins;

    private boolean activated = false;


    private int forcedTriggerDuration;


    public CounterManager(int triggerDuration, int maxJoinsPerSecond, String ipAddress, String secretKey, String blockNewJoins) {
        this.triggerDuration = triggerDuration;
        this.maxJoinsPerSecond = maxJoinsPerSecond;
        this.ipAddress = ipAddress;
        this.secretKey = secretKey;
        this.blockNewJoins = blockNewJoins;
        bloomAB.getServer().getScheduler().buildTask(bloomAB, this::resetConnectionCounts)
                .repeat(1L, TimeUnit.SECONDS)
                .schedule();

        bloomAB.getServer().getScheduler().buildTask(bloomAB, this::logCurrentCount)
                .repeat(1L, TimeUnit.SECONDS)
                .schedule();
    }


    private CompletableFuture<Boolean> makeHttpRequest(boolean isStop) {
        return CompletableFuture.supplyAsync(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL("https://abapi.bloom.host/");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("User-Agent", "BloomAB");
                conn.setDoOutput(true);

                String data;
                if (isStop) {
                    data = "STOP=1&ip_address=" + ipAddress + "&secret_key=" + secretKey;
                } else {
                    data = "triggerTime=" + this.forcedTriggerDuration + "&ip_address=" + ipAddress + "&block_new_joins=" + blockNewJoins + "&secret_key=" + secretKey + "&maxrps=" + maxJoinsPerSecond;
                }

                try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                    wr.writeBytes(data);
                    wr.flush();
                }

                int responseCode = conn.getResponseCode();

                if (responseCode == 200){
                    bloomAB.getLogger().info("Remote call successful: trigger " + (isStop ? "stopped." : "enabled."));
                    return true;
                } else {
                    bloomAB.getLogger().error("Remote call failed (HTTP Error "+ responseCode +"), trigger " + (isStop ? "stop" : "start") + " canceled.");
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
        bloomAB.getServer().getScheduler().buildTask(bloomAB, () -> {
            makeHttpRequest(true).thenAccept(success -> {
                if (!success) {
                    bloomAB.getLogger().info("Retry to stop the trigger failed, retrying in 1 minute.");
                    retryStopTrigger();
                } else {
                    bloomAB.getLogger().info("Retry to stop the trigger succeeded.");
                }
            });
        }).repeat(1, TimeUnit.MINUTES).schedule();
    }

    public CompletableFuture<Void> setForceTrigger(boolean forceTrigger, int seconds) {
        CompletableFuture<Void> exceptionFuture = new CompletableFuture<>();

        if ("<yourserverip>".equals(ipAddress) || "<yourkey>".equals(secretKey)) {
            exceptionFuture.completeExceptionally(new Exception("Plugin configuration is not configured! Please update the 'ip_address' and 'secret_key' values in the config file."));
            return exceptionFuture;
        }

        if (!"Enabled".equals(blockNewJoins) && !"Disabled".equals(blockNewJoins) && !"NewPlayersOnly".equals(blockNewJoins)) {
            exceptionFuture.completeExceptionally(new Exception("Invalid 'block_new_joins' configuration! It should be either 'Enabled', 'Disabled', or 'NewPlayersOnly'."));
            return exceptionFuture;
        }

        if (seconds > 3600) {
            exceptionFuture.completeExceptionally(new Exception("Trigger duration exceeds the maximum allowed value (3600 seconds)."));
            return exceptionFuture;
        }

        this.lastTriggerTime = System.currentTimeMillis() / 1000;
        this.forcedTriggerDuration = seconds == 0 ? triggerDuration : seconds;

        return makeHttpRequest(!forceTrigger).thenCompose(response -> {
            if (!response) {
                exceptionFuture.completeExceptionally(new Exception("Failed to make the HTTP request."));
                return exceptionFuture;
            }

            this.forceTrigger = forceTrigger;
            if (!forceTrigger) {
                return CompletableFuture.completedFuture(null);
            }
            bloomAB.getServer().getScheduler().buildTask(bloomAB, () -> {
                if (this.forceTrigger) {
                    this.forceTrigger = false;
                    makeHttpRequest(true).thenAccept(success -> { // make HTTP request when stopping the trigger
                        if (!success) {
                            bloomAB.getLogger().info("Failed to stop the trigger, scheduling retries.");
                            retryStopTrigger();
                        } else {
                            activated = false;
                            bloomAB.getLogger().info("Force trigger has been deactivated.");

                        }
                    });
                }
            }).delay(forcedTriggerDuration, TimeUnit.SECONDS).schedule();
            return CompletableFuture.completedFuture(null);
        });
    }
    public void incrementConnectionCount() {
        long currentTime = System.currentTimeMillis() / 1000;
        int currentCount = connectionCounts.getOrDefault(currentTime, 0);
        connectionCounts.put(currentTime, currentCount + 1);

        if ((currentCount + 1 > maxJoinsPerSecond || forceTrigger) && (currentTime - lastTriggerTime) > 1 && !activated) {
            lastTriggerTime = currentTime;
            activated = true;
            bloomAB.getLogger().warn("===================================== Limit reached! Trigger enabled. JPS: " + (currentCount + 1));
            setForceTrigger(true, triggerDuration).exceptionally(ex -> {
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
            bloomAB.getLogger().info("===================================== Number of RPS: " + currentCount);
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


}

