package host.bloom.ab.bukkit;

import dev.geri.konfig.util.InvalidConfigurationException;
import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.config.Config;
import host.bloom.ab.common.managers.CounterManager;
import host.bloom.ab.common.utils.Logger;
import host.bloom.ab.common.utils.Scheduler;
import host.bloom.ab.common.utils.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class BukkitPlugin extends JavaPlugin implements AbstractPlugin {

    private BukkitLogger logger;
    private Config config;
    private CounterManager manager;

    @Override
    public void onEnable() {
        // Initialize the logger
        this.logger = new BukkitLogger(super.getLogger());

        // Load the config
        try {
            this.config = Config.load(this, this.getDataFolder().getAbsolutePath());
        } catch (IOException | InvalidConfigurationException exception) {
            this.getABLogger().error("Unable to load config, shutting down: " + exception.getMessage());
            return;
        }

        // Check for new updates in the background
        UpdateChecker.handle(this);

        // Initialize the manager
        this.manager = new CounterManager(this);

        // Initialize the commands
        new BukkitCommandHandler(this);

        // Load the listener
        if (this.config.catchRawConnections) this.getABLogger().warning("The Paper version of the plugin does not support using raw connections, defaulting to using built in APIs!");
        this.getServer().getPluginManager().registerEvents(new BukkitLoginListener(this), this);
    }

    @Override
    public CounterManager getManager() {
        return this.manager;
    }

    @Override
    public Scheduler getScheduler() {
        return new BukkitScheduler(this);
    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public Logger getABLogger() {
        return this.logger;
    }

    @Override
    public Config getABConfig() {
        return this.config;
    }

}
