package host.bloom.ab.common.config;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class MessageKeys implements SettingsHolder {

    @Override
    public void registerComments(@NotNull CommentsConfiguration conf) {
        String[] header = {
                "If you have any questions, Please contact us at:",
                "",
                "Support: discord.gg/bloom"
        };

        conf.setComment("misc", header);
    }

    public static final Property<String> prefix = newProperty("misc.prefix", "<bold><gold>BloomAB</bold> <dark_gray>»");

    public static final Property<String> invalid_number = newProperty("misc.invalid-number", "{prefix} <red>Please enter a valid number!");

    public static final Property<String> invalid_usage = newProperty("misc.invalid-usage", "{prefix} <red>Invalid Usage! The correct usage is: <gold>{usage}");

    public static final Property<String> no_permission = newProperty("misc.no-permission", "{prefix} <red>You do not have the permission <gold>{permission}");

    public static final Property<String> reloaded_plugin = newProperty("misc.reloaded-plugin", "{prefix} <red>You have reloaded the plugin.");

    public static final Property<String> plugin_error = newProperty("misc.plugin-error", "{prefix} <red>Unable to perform action {action} with error: {error}");

    public static final Property<String> trigger_deactivating = newProperty("trigger.deactivating", "<red>Trigger is enabled. It will be deactivated in <dark_red>{count}</dark_red> seconds.");

    public static final Property<String> trigger_not_enabled = newProperty("trigger.not-enabled", "<red>Trigger is currently not enabled.");

    public static final Property<String> trigger_enabled = newProperty("trigger.enabled", "{prefix} <red>Trigger is already enabled!");

    public static final Property<String> trigger_failed = newProperty("trigger.failed", "{prefix} <red>Failed to enable trigger, Check the console for more details.");

    public static final Property<String> trigger_attempting = newProperty("trigger.attempting", "{prefix} <red>Attempting to enable the trigger...");

    public static final Property<String> trigger_manually_stopped = newProperty("trigger.manually-stopped", "{prefix} <red>Trigger manually stopped.");

    public static final Property<String> trigger_stopped = newProperty("trigger.stopped", "{prefix} <red>Trigger is already stopped.");

    public static final Property<String> trigger_exceeds_duration = newProperty("trigger.exceeds-duration", "{prefix} <red>Trigger duration exceeds the maximum allowed value (3600 seconds).");

    public static final Property<String> trigger_set_duration = newProperty("trigger.set-duration", "{prefix} <red>Trigger duration set to <gold>{duration} seconds!");

    public static final Property<String> action_bar = newProperty("trigger.action-bar", "{prefix} JPS {joins}/sec");

    public static final Property<String> exceeds_max_joins = newProperty("set.exceeds-max-joins", "{prefix} <red>Max joins per second exceeds the maximum allowed value (100000).");

    public static final Property<String> max_join_set = newProperty("set.max-join-set", "{prefix} <red>Max joins per second set to <gold>{value}!");

    public static final Property<String> set_location = newProperty("set.set-location", "{prefix} <yellow>Set location to<white>: <gold>{location}");

    public static final Property<String> invalid_location = newProperty("set.invalid-location", "{prefix} <red>Invalid location<white>: <dark_red>{location}");

    public static final Property<List<String>> help = newListProperty("misc.help", new ArrayList<String>() {{
        add("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        add("<bold><gold>BloomAB Commands</bold><reset>:");
        add("<yellow>/bab force <gold>[seconds]: <gray>Enable force trigger for X seconds");
        add("<yellow>/bab forcestop: <gray>Force stop the trigger and keep it disabled until the finish of the attack");
        add("<yellow>/bab set maxjps <gold>[number]: <gray>Set max joins per second");
        add("<yellow>/bab set duration <gold>[seconds]: <gray>Set trigger duration");
        add("<yellow>/bab set location <gold>[location]: <gray>Set server location");
        add("");
        add("<bold><gold>Connection Stats<reset>:");
        add("<gray>• <yellow>Trigger joins per second: <gold>{jps} rps");
        add("<gray>• <yellow>Trigger duration: <gold>{duration} seconds");
        add("<gray>• <yellow>Block new joins: <gold>{value}");
        add("<gray>• <yellow>Current joins per second: <gold>{cjps}");
        add("<gray>• {status}");
        add("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }});
}