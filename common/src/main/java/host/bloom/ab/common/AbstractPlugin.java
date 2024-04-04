package host.bloom.ab.common;

import ch.jalu.configme.SettingsManager;
import host.bloom.ab.common.config.ConfigKeys;
import host.bloom.ab.common.managers.ConfigManager;
import host.bloom.ab.common.managers.CounterManager;
import host.bloom.ab.common.utils.Logger;
import host.bloom.ab.common.utils.Scheduler;
import host.bloom.ab.common.utils.UpdateChecker;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface AbstractPlugin {

    enum Platform {
        BUKKIT,
        WATERFALL,
        VELOCITY
    }

    CounterManager getManager();

    Scheduler getScheduler();

    String getVersion();

    Logger getABLogger();

    Platform getPlatform();

    void actionbar(UUID uuid, String message);

    <P> P getPlayer(UUID uuid);
  
    File getFolder();

    int getPort();

    default void afterStartup() {

        // Ensure it's on a supported port
        List<Integer> supportedPorts = Arrays.asList(25565, 25566, 25567);
        if (!supportedPorts.contains(this.getPort())) {
            throw new RuntimeException("The server is not using a supported port! Please ensure it's using one of the following ports, and restart: " + supportedPorts.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        }

        SettingsManager config = ConfigManager.getConfig();

        this.getABLogger().info("Successfully loaded version: v" + this.getVersion() + ", location: " + config.getProperty(ConfigKeys.locations) + "!");

        // Check for new updates in the background
        UpdateChecker.handle(this);

        if (this.getPlatform() == Platform.BUKKIT && config.getProperty(ConfigKeys.catch_raw_connections)) {
            this.getABLogger().warning("The plugin does not support using raw connections for this platform, defaulting to using built in APIs!");
        }

        if (this.getPlatform() == Platform.WATERFALL && !config.getProperty(ConfigKeys.catch_raw_connections)) {
            this.getABLogger().warning("The plugin does not support using internal APIs for connections for this platform, defaulting to catching raw connections!");
        }
    }

}

