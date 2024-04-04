package host.bloom.ab.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.VelocityServer;
import com.velocitypowered.proxy.network.ConnectionManager;
import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.config.ConfigKeys;
import host.bloom.ab.common.managers.ConfigManager;
import host.bloom.ab.common.managers.CounterManager;
import host.bloom.ab.common.utils.Logger;
import host.bloom.ab.common.utils.Scheduler;
import host.bloom.ab.common.utils.Utils;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Plugin(id = "@id@", name = "@name@", version = "@version@", description = "@description@")
public class VelocityPlugin implements AbstractPlugin {

    private final ProxyServer server;
    private final VelocityLogger logger;
    private PluginContainer plugin;
    private CounterManager manager;

    @Inject
    public VelocityPlugin(ProxyServer server, org.slf4j.Logger logger) {
        this.server = server;
        this.logger = new VelocityLogger(logger);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Initialize the plugin
        this.plugin = server.getPluginManager().ensurePluginContainer(this);

        // Load the configuration files.
        ConfigManager.load(this);

        // Initialize the manager
        this.manager = new CounterManager(this);

        // Initialize the commands
        new VelocityCommandHandler(this, this.server.getCommandManager());

        this.server.getEventManager().register(this, new VelocityLoginListener(this));

        // We can either use the built-in event or our own channel listener
        if (ConfigManager.getConfig().getProperty(ConfigKeys.catch_raw_connections)) {
            // Initialize the login hook channel
            try {
                Class<?> velocityServerClass = VelocityServer.class;
                Field cmField = velocityServerClass.getDeclaredField("cm");
                cmField.setAccessible(true);

                // Initialize the connection manager
                ConnectionManager connectionManager = (ConnectionManager) cmField.get(server);
                VelocityLoginHookChannel channel = new VelocityLoginHookChannel(this, connectionManager.getServerChannelInitializer().get());
                connectionManager.getServerChannelInitializer().set(channel);

                // Handle other tasks
                this.afterStartup();
                return;
            } catch (Exception exception) {
                // Impossible to get exception, only for new version of Velocity.
                this.getABLogger().error("Unable to initialize raw connection catcher, using built-in events: " + exception.getMessage());
                exception.printStackTrace();
            }
        }

        // Handle other tasks
        this.afterStartup();
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
    public Platform getPlatform() {
        return Platform.VELOCITY;
    }

    @Override
    public File getFolder() {
        return Paths.get("plugins", plugin.getDescription().getName().get()).toFile();
    }

    @Override
    public void actionbar(UUID uuid, String message) {
        Optional<Player> player = this.server.getPlayer(uuid);

        player.ifPresent(value -> value.sendActionBar(Utils.color(message)));
    }

    @Override
    public Optional<Player> getPlayer(UUID uuid) {
        return this.server.getPlayer(uuid);
    }

    @Override
    public int getPort() {
        return this.server.getBoundAddress().getPort();
    }

}
