package host.bloom.ab.common.commands;

import ch.jalu.configme.SettingsManager;
import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.commands.sub.Force;
import host.bloom.ab.common.commands.sub.ForceStop;
import host.bloom.ab.common.commands.sub.Reload;
import host.bloom.ab.common.commands.sub.Set;
import host.bloom.ab.common.config.ConfigKeys;
import host.bloom.ab.common.config.enums.Messages;
import host.bloom.ab.common.managers.ConfigManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Handler {

    private final AbstractPlugin plugin;
    private final HashMap<String, SubCommand> commands = new HashMap<>();

    public Handler(AbstractPlugin plugin) {
        this.plugin = plugin;
        commands.put("forcestop", new ForceStop(plugin));
        commands.put("reload", new Reload());
        commands.put("force", new Force(plugin));
        commands.put("set", new Set());
    }

    public static String getCommandName() {
        return "bloomab";
    }

    public static String[] getAliases() {
        return new String[]{"bab", "blab"};
    }

    public void execute(Sender sender, String[] strings) {
        if (!sender.hasPermission("bab.admin")) {
            sender.sendMessage(Messages.no_permission.getMessage("{permission}", "bab.admin"));
            return;
        }

        if (strings.length == 0) {
            sendHelpMessage(sender);
            return;
        }

        if (!commands.containsKey(strings[0].toLowerCase())) {
            sendHelpMessage(sender);
            return;
        }

        SubCommand subCommand = commands.get(strings[0].toLowerCase());

        if (!sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(Messages.no_permission.getMessage("{permission}", subCommand.getPermission()));
            return;
        }

        try {
            subCommand.run(sender, strings);
        } catch (Exception exception) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{action}", "executing command");
            placeholders.put("{error}", exception.getMessage());

            sender.sendMessage(Messages.plugin_error.getMessage(placeholders));
        }
    }


    public List<String> onTabComplete(Sender sender, String[] args) {
        if (!sender.hasPermission("bab.admin")) return Collections.emptyList();

        // Return the subcommands
        if (args.length <= 1) return new ArrayList<>(commands.keySet());

        // See if it's a command
        SubCommand command = commands.get(args[0].toLowerCase());
        if (command == null) return Collections.emptyList();

        return command.getTabCompletion(args);
    }

    private void sendHelpMessage(Sender sender) {
        String triggerStatus;

        if (this.plugin.getManager().isForceTrigger()) {
            long remainingSeconds = this.plugin.getManager().getRemainingSeconds();
            triggerStatus = Messages.trigger_deactivating.getMessage("{count}", String.valueOf(remainingSeconds));
        } else {
            triggerStatus = Messages.trigger_not_enabled.getMessage();
        }

        SettingsManager config = ConfigManager.getConfig();

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{jps}", String.valueOf(config.getProperty(ConfigKeys.max_joins_per_second)));
        placeholders.put("{duration}", String.valueOf(config.getProperty(ConfigKeys.trigger_duration)));
        placeholders.put("{value}", String.valueOf(config.getProperty(ConfigKeys.block_new_joins)));
        placeholders.put("{cjps}", String.valueOf(this.plugin.getManager().getCurrentCount(System.currentTimeMillis() / 1000)));
        placeholders.put("{status}", triggerStatus);

        sender.sendMessage(Messages.help.getMessage(placeholders));
    }
}