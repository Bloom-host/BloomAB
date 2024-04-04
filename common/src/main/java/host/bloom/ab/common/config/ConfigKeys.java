package host.bloom.ab.common.config;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import host.bloom.ab.common.config.enums.BlockNewJoins;
import host.bloom.ab.common.config.enums.Location;
import org.jetbrains.annotations.NotNull;
import static ch.jalu.configme.properties.PropertyInitializer.newBeanProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class ConfigKeys implements SettingsHolder {

    protected ConfigKeys() {}

    @Override
    public void registerComments(@NotNull CommentsConfiguration conf) {
        String[] header = {
                "If you have any questions, Please contact us at:",
                "https://bloom.host/support"
        };

        conf.setComment("limits", header);
    }


    public static final Property<Integer> trigger_duration = newProperty("limits.trigger-duration", 120);

    public static final Property<Integer> max_joins_per_second = newProperty("limits.max-joins-per-second", 120);

    public static final Property<String> api_endpoint = newProperty("api.endpoint", "disabled");

    public static final Property<String> api_secret = newProperty("api.secret", "disabled");

    @Comment("Supported locations: ASHBURN, LOS_ANGELES, GERMANY")
    public static final Property<Location> locations = newBeanProperty(Location.class, "options.location", Location.ASHBURN);

    public static final Property<Boolean> catch_raw_connections = newProperty("options.catch-raw-connections", true);

    @Comment("Supported methods: ENABLED, NEW_PLAYERS_ONLY, DISABLED")
    public static final Property<BlockNewJoins> block_new_joins = newBeanProperty(BlockNewJoins.class, "options.block-new-joins", BlockNewJoins.NEW_PLAYERS_ONLY);

    public static final Property<Boolean> check_for_updates = newProperty("options.check-for-updates", true);

}