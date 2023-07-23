package me.bloomab.configs;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.configurationdata.CommentsConfiguration;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class MainConfiguration implements SettingsHolder {

    @Comment("max 3600, default: 60")
    public static final Property<Integer> TRIGGER_DURATION =
            newProperty("trigger_duration", 60);

    @Comment("threshold for the activation of the trigger, default: 30")
    public static final Property<Integer> MAX_JOINS_PER_SECOND =
            newProperty("max_joins_per_second", 30);

    @Comment("Used for special cases, default: NotInUse")
    public static final Property<String> IP_ADDRESS =
            newProperty("ip_address", "NotInUse");

    @Comment("Used for special cases, default: NotInUse")
    public static final Property<String> SECRET_KEY =
            newProperty("secret_key", "NotInUse");

    @Comment("Enabled/Disabled/NewPlayersOnly, default: NewPlayersOnly")
    public static final Property<String> BLOCK_NEW_JOINS =
            newProperty("block_new_joins", "NewPlayersOnly");

    @Comment("enable raw connections mode, true/false, default: false")
    public static final Property<Boolean> CATCH_RAW_CONNECTIONS =
            newProperty("catch_raw_connections", false);

    private MainConfiguration() {
        // prevent instantiation
    }

    @Override
    public void registerComments(CommentsConfiguration conf) {
        conf.setComment("", "Configuration file BloomAB");
    }
}
