package host.bloom.ab.common.config;

import dev.geri.konfig.Konfig;
import dev.geri.konfig.util.InvalidConfigurationException;
import host.bloom.ab.common.AbstractPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class Config {

    public static final String FILE_NAME = "config.yml";
    private transient final Konfig konfig;

    public int triggerDuration;
    public int maxJoinsPerSecond;
    public String ipAddress;
    public String secretKey;
    public BlockNewJoins blockNewJoins;

    public Config(AbstractPlugin plugin, Konfig konfig) {
        this.konfig = konfig;
        this.triggerDuration = konfig.getInt("trigger_duration");
        this.maxJoinsPerSecond = konfig.getInt("max_joins_per_second");
        this.ipAddress = konfig.getString("ip_address");
        this.secretKey = konfig.getString("secret_key");

        String blockNewJoinsRaw = konfig.getString("block_new_joins");
        BlockNewJoins blockNewJoins;
        try {
            this.blockNewJoins = BlockNewJoins.valueOf(blockNewJoinsRaw);
        } catch (IllegalArgumentException exception) {
            plugin.getLogger().warning("Invalid value " + blockNewJoinsRaw + ", using default!");
            this.blockNewJoins = BlockNewJoins.NEW_PLAYERS_ONLY;
            try {
                this.save();
            } catch (IOException ex) {
                plugin.getLogger().warning("Unable to save configuration: " + ex.getMessage());
            }
        }
    }

    public void save() throws IOException {
        konfig.set("trigger_duration", this.triggerDuration);
        konfig.set("max_joins_per_second", this.maxJoinsPerSecond);
        konfig.set("ip_address", this.ipAddress);
        konfig.set("secret_key", this.secretKey);
        konfig.set("block_new_joins", this.blockNewJoins != null ? this.blockNewJoins.name() : null);
        this.konfig.options().copyDefaults(true);
        this.konfig.save();
    }

    public static Config load(AbstractPlugin plugin, String directory) throws IOException, InvalidConfigurationException {

        // If the config does not exist, we will create it
        String path = directory + File.separator + FILE_NAME;
        File file = new File(path);
        if (!file.exists()) {
            try {
                // Create the directory
                File parent = file.getParentFile();
                if (!parent.exists() && !parent.mkdirs()) throw new IOException("Unable to create plugin directory!");

                // Save the default config
                Files.copy(plugin.getClass().getClassLoader().getResourceAsStream(FILE_NAME), file.toPath());
            } catch (IOException exception) {
                throw new IOException("Unable to save default config: " + exception.getMessage());
            }
        }

        // Load the config
        return new Config(plugin, Konfig.loadConfiguration(file));
    }

}
