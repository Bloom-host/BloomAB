package host.bloom.ab.waterfall;

import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.config.Config;
import host.bloom.ab.common.managers.CounterManager;
import host.bloom.ab.common.utils.Scheduler;
import host.bloom.ab.common.utils.UpdateChecker;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.util.logging.Logger;

public class WaterfallPlugin extends Plugin implements AbstractPlugin {

    private Config config;
    private CounterManager counterManager;
    private Scheduler scheduler;

    @Override
    public void onEnable() {
        // Load the config
        try {
            this.config = Config.load(this.getDataFolder().getAbsolutePath());
        } catch (IOException exception) {
            this.getLogger().severe("Unable to load config, shutting down: " + exception.getMessage());
            return;
        }

        // Check for new updates in the background
        UpdateChecker.handle(this);

        // Initialize the manager
        this.counterManager = new CounterManager(this);

        // Initialize the commands
        getProxy().getPluginManager().registerCommand(this, new WaterfallCommandHandler(this));

        // Load the pipeline injector
        new WaterfallPipelineInjector(counterManager);
    }

    @Override
    public CounterManager getManager() {
        return this.counterManager;
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
    public Logger getLogger() {
        return super.getLogger();
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

}
