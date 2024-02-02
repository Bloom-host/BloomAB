package host.bloom.ab.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.File;
import java.io.IOException;

public final class Config {

    public static final String FILE_NAME = "config.yml";

    private transient String path;
    private final int triggerDuration;
    private final int maxJoinsPerSecond;
    private final String ipAddress;
    private final String secretKey;
    private final BlockNewJoins blockNewJoins;

    public Config(int triggerDuration, int maxJoinsPerSecond, String ipAddress, String secretKey, BlockNewJoins blockNewJoins) {
        this.triggerDuration = triggerDuration;
        this.maxJoinsPerSecond = maxJoinsPerSecond;
        this.ipAddress = ipAddress;
        this.secretKey = secretKey;
        this.blockNewJoins = blockNewJoins;
    }

    public static Config load(String directory) throws IOException {

        // If the config does not exist, we will create it
        String path = directory + File.separator + FILE_NAME;
        File file = new File(path);
        if (!file.exists()) {
            // We will initialize some default values here
            Config config = new Config(120, 120, "NotInUse", "NotInUse", BlockNewJoins.NEW_PLAYERS_ONLY);

            try {
                // Create the directory
                if (!file.mkdirs()) throw new IOException("Unable to create plugin directory!");

                // Save the config
                config.save();
            } catch (IOException exception) {
                throw new IOException("Unable to save default config: " + exception.getMessage());
            }
        }

        // Load the config
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        Config config = mapper.readValue(file, Config.class);
        config.path = path;

        return config;
    }

    public void save() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        mapper.writeValue(new File(this.path), this);
    }

    public int getTriggerDuration() {
        return triggerDuration;
    }

    public int getMaxJoinsPerSecond() {
        return maxJoinsPerSecond;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public BlockNewJoins getBlockNewJoins() {
        return blockNewJoins;
    }

    public void setTriggerDuration(int duration) {
        // Todo (notgeri):
    }

    public void setMaxJoinsPerSecond(int maxJps) {
        // Todo (notgeri):
    }
}
