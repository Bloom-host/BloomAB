package host.bloom.ab.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.VelocityServer;
import com.velocitypowered.proxy.network.ConnectionManager;
import dev.geri.konfig.util.InvalidConfigurationException;
import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.config.Config;
import host.bloom.ab.common.managers.CounterManager;
import host.bloom.ab.common.utils.Logger;
import host.bloom.ab.common.utils.Scheduler;
import host.bloom.ab.common.utils.UpdateChecker;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Paths;

@Plugin(id = "@id@", name = "@name@", version = "@version@", description = "@description@")
public class VelocityPlugin implements AbstractPlugin {

    private final ProxyServer server;
    private final VelocityLogger logger;
    private PluginContainer plugin;
    private CounterManager manager;
    private Config config;

    @Inject
    public VelocityPlugin(ProxyServer server, org.slf4j.Logger logger) {
        this.server = server;
        this.logger = new VelocityLogger(logger);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        // Initialize the plugin
        this.plugin = server.getPluginManager().ensurePluginContainer(this);

        // Load the config
        try {
            this.config = Config.load(this, Paths.get("plugins", plugin.getDescription().getName().get()).toAbsolutePath().toString());
        } catch (IOException | InvalidConfigurationException exception) {
            this.getABLogger().error("Unable to load config, shutting down: " + exception.getMessage());
            return;
        }

        // Check for new updates in the background
        UpdateChecker.handle(this);

        // Initialize the manager
        this.manager = new CounterManager(this);

        // Initialize the commands
        new VelocityCommandHandler(this, this.server.getCommandManager());

        // We can either use the built-in event or our own channel listener
        if (this.config.catchRawConnections) {

            // Impossible to get exception, only for new version of Velocity.
            try {
                Class<?> velocityServerClass = VelocityServer.class;
                Field cmField = velocityServerClass.getDeclaredField("cm");
                cmField.setAccessible(true);

                // Get the connection manager
                ConnectionManager connectionManager = (ConnectionManager) cmField.get(server);

                // Initialize the login hook channel
                VelocityLoginHookChannel loginHookChannel = new VelocityLoginHookChannel(this, connectionManager.getServerChannelInitializer().get());
                connectionManager.getServerChannelInitializer().set(loginHookChannel);
                return;

            } catch (IllegalAccessException | NoSuchFieldException exception) {
                this.getABLogger().error("Unable to initialize raw connection catcher, using built-in events: " + exception.getMessage());
                exception.printStackTrace();
            }
        }

        server.getEventManager().register(this, new VelocityLoginListener(this));
    }

    @Override
    public CounterManager getManager() {
        return this.manager;
    }

    @Override
    public Scheduler getScheduler() {
        return new VelocityScheduler(this, server.getScheduler());
    }

    @Override
    public String getVersion() {
        return this.plugin.getDescription().getVersion().orElse(null);
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
