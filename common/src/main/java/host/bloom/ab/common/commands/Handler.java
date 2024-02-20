package host.bloom.ab.common.commands;

import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.commands.sub.Force;
import host.bloom.ab.common.commands.sub.ForceStop;
import host.bloom.ab.common.commands.sub.Set;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Handler {

    private final AbstractPlugin plugin;
    private final HashMap<String, SubCommand> commands = new HashMap<>();

    public Handler(AbstractPlugin plugin) {
        this.plugin = plugin;
        commands.put("forcestop", new ForceStop(plugin));
        commands.put("force", new Force(plugin));
        commands.put("set", new Set(plugin));
    }

    public static String getCommandName() {
        return "bloomab";
    }

    public static String[] getAliases() {
        return new String[]{"bab", "blab"};
    }

    public void execute(Sender sender, String[] strings) {

        if (!sender.hasPermission("bab.admin")) {
            sender.sendMessage("§cYou don't have enough permissions to execute this command!");
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
            sender.sendMessage("§cYou don't have enough permissions to execute this command!");
            return;
        }

        try {
            subCommand.run(sender, strings);
        } catch (Exception exception) {
            sender.sendMessage("Failed to execute command: " + exception.getMessage());
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
        if (plugin.getManager().isForceTrigger()) {
            long remainingSeconds = plugin.getManager().getRemainingSeconds();
            triggerStatus = "§cTrigger is enabled. It will be deactivated in §4" + remainingSeconds + "§c seconds.";
        } else {
            triggerStatus = "§aTrigger is not currently enabled.";
        }

		String[] messageLines = {
			"§8§m                                                          §r",
			"§6§lBloomAB COMMANDS§r:",
			"§e/bab force §6<seconds>§r: §7Enable force trigger for X seconds",
			"§e/bab forcestop§r: §7Force stop the trigger and keep it disabled until the finish of the attack",
			"§e/bab set maxjps §6<number>§r: §7Set max joins per second",
			"§e/bab set duration §6<seconds>§r: §7Set trigger duration",
			"§e/bab set location §6<location>§r: §7Set server location",
			"§r",
			"§6§lCONNECTION STATS§r:",
			"§7• §eTrigger joins per second: §6%s rps",
			"§7• §eTrigger duration: §6%s seconds",
			"§7• §eBlock new joins: §6%s",
			"§7• §eCurrent joins per second: §6%s",
			"§7• %s",
			"§8§m                                                          §r"
		};

		String formattedMessage = String.format(
			String.join("%n", messageLines),
			plugin.getABConfig().maxJoinsPerSecond,
			plugin.getABConfig().triggerDuration,
			plugin.getABConfig().blockNewJoins,
			plugin.getManager().getCurrentCount(System.currentTimeMillis() / 1000),
			triggerStatus
		);

		sender.sendMessage(formattedMessage);

    }
}
