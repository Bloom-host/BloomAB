package me.bloomab;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import com.google.inject.Inject;
import com.sun.tools.javac.Main;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.VelocityServer;
import com.velocitypowered.proxy.network.ConnectionManager;
import lombok.Getter;
import lombok.SneakyThrows;
import me.bloomab.configs.MainConfiguration;
import me.bloomab.listeners.LoginHookChannel;
import me.bloomab.listeners.LoginListener;
import me.bloomab.managers.CounterManager;
import me.bloomab.utils.UpdateChecker;
import org.slf4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;

@Plugin(
        id = "bloomab",
        name = "BloomAB",
        version = "1.2",
        authors = "BloomAB")
public class BloomAB  {
    @Getter private static BloomAB instance;
    private PluginContainer plugin;

    @Getter private final ProxyServer server;
    @Getter private final Logger logger;
    @Getter private CounterManager counterManager;
    @Getter private SettingsManager configuration;

    @Inject
    public BloomAB(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
        plugin = getServer().getPluginManager().ensurePluginContainer(this);

        logger.info("Initializing BloomAB plugin!");
        String pluginVersion = plugin.getDescription().getVersion().orElse("N/D");
        try {
            loadFile();

            new UpdateChecker(this, "https://abapi.bloom.host/velocity_version/").getVersion(version -> {
                if (pluginVersion.equals(version)) {
                    //getLogger().info("There is not a new update available.");
                } else {
                    try {
                        URL url = new URL("https://abapi.bloom.host/velocity_downloadUrl/");
                        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                        String downloadUrl = in.readLine();  // read first line
                        in.close();

                        // Checking if downloadUrl is not null before logging the message
                        if (downloadUrl != null) {
                            getLogger().info("There is a new update available. Please download it at: " + downloadUrl);
                        } else {
                            //getLogger().severe("Download URL is null. Please check the source.");
                        }
                    } catch (IOException e) {
                        //getLogger().severe("An error occurred while fetching the download URL: " + e.getMessage());
                    }
                }
            });

            int triggerDuration = configuration.getProperty(MainConfiguration.TRIGGER_DURATION);
            int maxJoinsPerSecond = configuration.getProperty(MainConfiguration.MAX_JOINS_PER_SECOND);
            String ipAddress = configuration.getProperty(MainConfiguration.IP_ADDRESS);
            String secretKey = configuration.getProperty(MainConfiguration.SECRET_KEY);
            String blockNewJoins = configuration.getProperty(MainConfiguration.BLOCK_NEW_JOINS);

            counterManager = new CounterManager(triggerDuration, maxJoinsPerSecond, ipAddress, secretKey, blockNewJoins);

            if (configuration.getProperty(MainConfiguration.CATCH_RAW_CONNECTIONS)) {
                Field cmField;
                Class<?> velocityServerClass = VelocityServer.class;
                try {
                    cmField = velocityServerClass.getDeclaredField("cm");
                    cmField.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e); // Impossible to get exception, only for new version of Velocity.
                }

                ConnectionManager connectionManager;

                try {
                    connectionManager = (ConnectionManager) cmField.get(server);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e); // Impossible to get exception, only for new version of Velocity.
                }

                LoginHookChannel loginHookChannel = new LoginHookChannel((VelocityServer) server, counterManager);
                //noinspection deprecation
                connectionManager.getServerChannelInitializer().set(loginHookChannel);
            } else {
                server.getEventManager().register(this, new LoginListener(this));
            }

            CommandManager commandManager = server.getCommandManager();
            // Here you can add meta for the command, as aliases and the plugin to which it belongs (RECOMMENDED)
            CommandMeta commandMeta = commandManager.metaBuilder("bloomab")
                    // This will create a new alias for the command "/test"
                    // with the same arguments and functionality
                    .aliases("bab")
                    .plugin(this)
                    .build();

            // You can replace this with "new EchoCommand()" or "new TestCommand()"
            // SimpleCommand simpleCommand = new TestCommand();
            // RawCommand rawCommand = new EchoCommand();
            // The registration is done in the same way, since all 3 interfaces implement "Command"

            // Finally, you can register the command
            commandManager.register(commandMeta, new me.bloomab.commands.CommandManager(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // more initialization code here
    }

    @SneakyThrows
    private void loadFile() {
        Path pluginFolder = Paths.get("plugins", plugin.getDescription().getName().orElseThrow());
        Files.createDirectories(pluginFolder);
        Path configFile = pluginFolder.resolve("config.yml");
        configuration = SettingsManagerBuilder
                .withYamlFile(configFile.toFile())
                .configurationData(MainConfiguration.class)
                .useDefaultMigrationService()
                .create();
    }
}
