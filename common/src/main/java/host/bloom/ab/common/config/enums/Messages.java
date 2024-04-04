package host.bloom.ab.common.config.enums;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.Property;
import host.bloom.ab.common.config.MessageKeys;
import host.bloom.ab.common.managers.ConfigManager;
import host.bloom.ab.common.utils.Utils;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Messages {

    prefix(MessageKeys.prefix),
    invalid_number(MessageKeys.invalid_number),
    invalid_usage(MessageKeys.invalid_usage),
    no_permission(MessageKeys.no_permission),
    reloaded_plugin(MessageKeys.reloaded_plugin),
    plugin_error(MessageKeys.plugin_error),
    trigger_deactivating(MessageKeys.trigger_deactivating),
    trigger_not_enabled(MessageKeys.trigger_not_enabled),
    trigger_enabled(MessageKeys.trigger_enabled),
    trigger_failed(MessageKeys.trigger_failed),
    trigger_attempting(MessageKeys.trigger_attempting),
    trigger_manually_stopped(MessageKeys.trigger_manually_stopped),
    trigger_stopped(MessageKeys.trigger_stopped),
    trigger_exceeds_duration(MessageKeys.trigger_exceeds_duration),
    trigger_set_duration(MessageKeys.trigger_set_duration),
    action_bar(MessageKeys.action_bar),
    action_bar_enabled(MessageKeys.action_bar_enabled),
    action_bar_disabled(MessageKeys.action_bar_disabled),
    exceeds_max_joins(MessageKeys.exceeds_max_joins),
    max_join_set(MessageKeys.max_join_set),
    set_location(MessageKeys.set_location),
    invalid_location(MessageKeys.invalid_location),
    help(MessageKeys.help, true);

    private Property<String> property;

    private Property<List<String>> properties;

    private boolean isList = false;

    Messages(Property<String> property) {
        this.property = property;
    }

    Messages(Property<List<String>> properties, boolean isList) {
        this.properties = properties;
        this.isList = isList;
    }

    private final @NotNull SettingsManager messages = ConfigManager.getMessages();

    public @NotNull String getString() {
        return this.messages.getProperty(this.property);
    }

    public @NotNull List<String> getList() {
        return this.messages.getProperty(this.properties);
    }

    public boolean isList() {
        return this.isList;
    }

    public String getMessage() {
        return getMessage(new HashMap<>());
    }

    public String getMessage(String placeholder, String replacement) {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put(placeholder, replacement);

        return getMessage(placeholders);
    }

    public String getMessage(Map<String, String> placeholders) {
        return parse(placeholders).replaceAll("\\{prefix}", this.messages.getProperty(MessageKeys.prefix));
    }

    private String parse(Map<String, String> placeholders) {
        String message;

        if (isList()) {
            message = Utils.convertList(getList());
        } else {
            message = getString();
        }

        if (!placeholders.isEmpty()) {
            for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                message = message.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
            }
        }

        return message;
    }
}