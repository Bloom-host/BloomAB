package host.bloom.ab.bukkit;

import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.managers.ConfigManager;
import host.bloom.ab.common.managers.CounterManager;
import host.bloom.ab.common.utils.Logger;
import host.bloom.ab.common.utils.Scheduler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.io.File;

public class BukkitPlugin extends JavaPlugin implements AbstractPlugin {

    private BukkitAudiences adventure;

    private BukkitLogger logger;
    private CounterManager manager;

    @Override
    public void onEnable() {
        // Initialize the logger
        this.logger = new BukkitLogger(super.getLogger());

        // Load the configuration files.
        ConfigManager.load(this);

        // Initialize an audiences instance for the plugin
        this.adventure = BukkitAudiences.create(this);

        // Initialize the manager
        this.manager = new CounterManager(this);

        // Initialize the commands
        new BukkitCommandHandler(this);

        // Load the listener
        this.getServer().getPluginManager().registerEvents(new BukkitLoginListener(this), this);

        // Handle other tasks
        this.afterStartup();
    }

    @Override
    public void onDisable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
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
    public Platform getPlatform() {
        return Platform.BUKKIT;
    }

    @Override
    public File getFolder() {
        return getDataFolder();
    }

    @Override
    public int getPort() {
        return this.getServer().getPort();
    }

    public @NotNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }

        return this.adventure;
    }
}
