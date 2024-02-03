package host.bloom.ab.waterfall;

import dev.geri.konfig.util.InvalidConfigurationException;
import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.config.Config;
import host.bloom.ab.common.managers.CounterManager;
import host.bloom.ab.common.utils.Logger;
import host.bloom.ab.common.utils.Scheduler;
import host.bloom.ab.common.utils.UpdateChecker;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;

public class WaterfallPlugin extends Plugin implements AbstractPlugin {

    private WaterfallLogger logger;
    private Config config;
    private CounterManager manager;
    private Scheduler scheduler;

    @Override
    public void onEnable() {

        // Initialize the logger
        this.logger = new WaterfallLogger(super.getLogger());

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
        getProxy().getPluginManager().registerCommand(this, new WaterfallCommandHandler(this));

        // Load the pipeline injector
        new WaterfallPipelineInjector(manager);
    }

    @Override
    public CounterManager getManager() {
        return this.manager;
    }

    @Override
    public Scheduler getScheduler() {
        if (this.scheduler == null) this.scheduler = new WaterfallScheduler(this);
        return this.scheduler;
    }

    @Override
    public String getVersion() {
        return this.getDescription().getVersion();
    }

    @Override
    public Logger getABLogger() {
        return this.logger;
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

}
