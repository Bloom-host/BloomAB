package host.bloom.ab.common.managers;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.config.ConfigKeys;
import host.bloom.ab.common.config.MessageKeys;
import java.io.File;

public class ConfigManager {

    private static SettingsManager config;
    private static SettingsManager messages;

    public static void load(AbstractPlugin plugin) {
        config = SettingsManagerBuilder
                .withYamlFile(new File(plugin.getFolder(), "config.yml"), getBuilder())
                .useDefaultMigrationService()
                .configurationData(ConfigKeys.class)
                .create();

        messages = SettingsManagerBuilder
                .withYamlFile(new File(plugin.getFolder(), "messages.yml"), getBuilder())
                .useDefaultMigrationService()
                .configurationData(MessageKeys.class)
                .create();
    }

    public static void reload() {
        config.reload();

        messages.reload();
    }

    public static SettingsManager getConfig() {
        return config;
    }

    public static SettingsManager getMessages() {
        return messages;
    }

    public static YamlFileResourceOptions getBuilder() {
        return YamlFileResourceOptions.builder().indentationSize(2).build();
    }
}