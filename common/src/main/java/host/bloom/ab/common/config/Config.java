package host.bloom.ab.common.config;

import dev.geri.konfig.Konfig;
import dev.geri.konfig.util.InvalidConfigurationException;
import host.bloom.ab.common.AbstractPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class Config {

    public static final String FILE_NAME = "config.yml";
    private transient final Konfig konfig;

    public int triggerDuration;
    public int maxJoinsPerSecond;
    public String ipAddress;
    public String secretKey;
    public BlockNewJoins blockNewJoins;
    public Location location;
    public boolean catchRawConnections;
    public boolean checkForUpdates;

    public Config(AbstractPlugin plugin, Konfig konfig) {
        this.konfig = konfig;
        this.triggerDuration = konfig.getInt("limits.trigger-duration");
        this.maxJoinsPerSecond = konfig.getInt("limits.max-joins-per-second");
        this.ipAddress = konfig.getString("api.endpoint");
        this.secretKey = konfig.getString("api.secret");
        this.catchRawConnections = konfig.getBoolean("options.catch-raw-connections");
        this.checkForUpdates = konfig.getBoolean("options.check-for-updates");

        boolean save = false;

        String locationRaw = konfig.getString("options.location");
        try {
            this.location = Location.valueOf(locationRaw);
        } catch (IllegalArgumentException | NullPointerException exception) {
            plugin.getABLogger().warning("Invalid location " + locationRaw + ", using default!");
            this.location = Location.ASHBURN;
            save = true;
        }

        String blockNewJoinsRaw = konfig.getString("options.block-new-joins");
        try {
            this.blockNewJoins = BlockNewJoins.valueOf(blockNewJoinsRaw);
        } catch (IllegalArgumentException | NullPointerException exception) {
            plugin.getABLogger().warning("Invalid value " + blockNewJoinsRaw + ", using default!");
            this.blockNewJoins = BlockNewJoins.NEW_PLAYERS_ONLY;
            save = true;
        }

        if (save) {
            try {
                this.save();
            } catch (IOException exception) {
                plugin.getABLogger().warning("Unable to save fixed configuration: " + exception.getMessage());
            }
        }
    }

    public void save() throws IOException {
        konfig.set("limits.trigger-duration", this.triggerDuration);
        konfig.set("limits.max-joins-per-second", this.maxJoinsPerSecond);
        konfig.set("api.endpoint", this.ipAddress);
        konfig.set("api.secret", this.secretKey);
        konfig.set("options.location", this.blockNewJoins != null ? this.blockNewJoins.name() : null);
        konfig.set("options.catch-raw-connections", this.catchRawConnections);
        konfig.set("options.check-for-updates", this.checkForUpdates);
        konfig.set("options.block-new-joins", this.blockNewJoins != null ? this.blockNewJoins.name() : null);

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
                try (InputStream resource = plugin.getClass().getClassLoader().getResourceAsStream(FILE_NAME)) {
                    if (resource == null) throw new IOException("unable to find config in JAR!");
                    Files.copy(resource, file.toPath());
                }
            } catch (IOException exception) {
                throw new IOException("Unable to save default config: " + exception.getMessage());
            }
        }

        // Load the config
        return new Config(plugin, Konfig.loadConfiguration(file));
    }

}
