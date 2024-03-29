package host.bloom.ab.common;

import host.bloom.ab.common.config.Config;
import host.bloom.ab.common.managers.CounterManager;
import host.bloom.ab.common.utils.Logger;
import host.bloom.ab.common.utils.Scheduler;
import host.bloom.ab.common.utils.UpdateChecker;

import java.util.Arrays;
import java.util.List;
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

    Config getABConfig();

    Platform getPlatform();

    int getPort();

    default void afterStartup() {

        // Ensure it's on a supported port
        List<Integer> supportedPorts = Arrays.asList(25565, 25566, 25567);
        if (!supportedPorts.contains(this.getPort())) {
            throw new RuntimeException("The server is not using a supported port! Please ensure it's using one of the following ports, and restart: " + supportedPorts.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        }

        this.getABLogger().info("Successfully loaded version: v" + this.getVersion() + ", location: " + this.getABConfig().location.getDisplayName() + "!");

        // Check for new updates in the background
        UpdateChecker.handle(this);

        if (this.getPlatform() == Platform.BUKKIT && this.getABConfig().catchRawConnections) {
            this.getABLogger().warning("The plugin does not support using raw connections for this platform, defaulting to using built in APIs!");
        }

        if (this.getPlatform() == Platform.WATERFALL && !this.getABConfig().catchRawConnections) {
            this.getABLogger().warning("The plugin does not support using internal APIs for connections for this platform, defaulting to catching raw connections!");
        }
    }

}

