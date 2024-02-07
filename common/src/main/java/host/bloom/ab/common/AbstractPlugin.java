package host.bloom.ab.common;

import host.bloom.ab.common.config.Config;
import host.bloom.ab.common.managers.CounterManager;
import host.bloom.ab.common.utils.Logger;
import host.bloom.ab.common.utils.Scheduler;
import host.bloom.ab.common.utils.UpdateChecker;

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

    default void afterStartup() {
        this.getABLogger().info("Successfully loaded version: v%s, location: %s!".formatted(this.getVersion(), this.getABConfig().location.getDisplayName()));

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

