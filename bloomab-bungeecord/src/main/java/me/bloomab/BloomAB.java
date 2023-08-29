package me.bloomab;

import lombok.Getter;
import lombok.SneakyThrows;
import me.bloomab.commands.CommandManager;
import me.bloomab.enums.BungeeConfig;
import me.bloomab.listeners.LoginListener;
import me.bloomab.managers.CounterManager;
import me.bloomab.objects.TextFile;
import me.bloomab.utils.UpdateChecker;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class BloomAB extends Plugin {

    @Getter
    private static BloomAB instance;

    @Getter
    private CounterManager counterManager;

    @Getter
    private TextFile configFile;

    @Override
    public void onEnable() {

        try {
            new UpdateChecker(this, "https://abapi.bloom.host/version/").getVersion(version -> {
                if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    //getLogger().info("There is not a new update available.");
                } else {
                    try {
                        URL url = new URL("https://abapi.bloom.host/downloadUrl/");
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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }



        instance = this;
        configFile = new TextFile(this, "config.yml");

        int triggerDuration = BungeeConfig.TRIGGER_DURATION.getInt();
        int maxJoinsPerSecond = BungeeConfig.MAX_JOINS_PER_SECOND.getInt();
        String ipAddress = BungeeConfig.IP_ADDRESS.getString();
        String secretKey = BungeeConfig.SECRET_KEY.getString();
        String block_new_joins = BungeeConfig.BLOCK_NEW_JOINS.getString();

        counterManager = new CounterManager(triggerDuration, maxJoinsPerSecond, ipAddress, secretKey, block_new_joins);

        getProxy().getPluginManager().registerCommand(this, new CommandManager(this));


        getProxy().getPluginManager().registerListener(this, new LoginListener(this));


        getLogger().info("Plugin enabled.");
    }

    @Override
    public void onDisable() {
    }





    @SneakyThrows
    public void saveConfiguration() {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(getConfigFile().getConfig(), new File(getDataFolder() + "/" + "config.yml"));
    }
}
