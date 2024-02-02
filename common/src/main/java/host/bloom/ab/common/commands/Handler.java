package host.bloom.ab.common.commands;

import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.commands.sub.ForceStop;
import host.bloom.ab.common.commands.sub.ForceSub;
import host.bloom.ab.common.commands.sub.Set;
import host.bloom.ab.common.config.BlockNewJoins;

import java.util.Collections;
import java.util.HashMap;

public class Handler {

    private final AbstractPlugin plugin;
    private final HashMap<String, SubCommand> commands = new HashMap<>();

    public Handler(AbstractPlugin plugin) {
        this.plugin = plugin;
        commands.put("forcestop", new ForceStop(plugin));
        commands.put("force", new ForceSub(plugin));
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
        } catch (Exception e) {
            sender.sendMessage("Failed to execute command: " + e.getMessage());
        }
    }


    public Iterable<String> onTabComplete(Sender sender, String[] args) {
        if (!sender.hasPermission("bab.admin")) return null;

        // Return the subcommands
        if (args.length <= 1) return commands.keySet();

        // See if it's a command
        SubCommand command = commands.get(args[0].toLowerCase());
        if (command == null) return null;

        return command.getTabCompletion(args);
    }

    private void sendHelpMessage(Sender sender) {
        sender.sendMessage("=========");
        sender.sendMessage("BloomAB Commands");
        sender.sendMessage("=========");
        sender.sendMessage("/bab force <seconds>: Enable force trigger for <seconds> seconds.");
        sender.sendMessage("/bab forcestop: Force stop the trigger and keep it disabled until the finish of the attack.");
        sender.sendMessage("/bab set maxjps <number>: Set max joins per second.");
        sender.sendMessage("/bab set duration <seconds>: Set trigger duration.");
        sender.sendMessage("=========");
        sender.sendMessage("Connections stats");
        sender.sendMessage("=========");
        int maxRPS = plugin.getConfig().getMaxJoinsPerSecond();
        int triggerDuration = plugin.getConfig().getTriggerDuration();
        BlockNewJoins block_new_joins = plugin.getConfig().getBlockNewJoins(); //thanks <3
        sender.sendMessage("Trigger joins Per Second: " + maxRPS + "rps");
        sender.sendMessage("Trigger Duration: " + triggerDuration + " seconds");
        sender.sendMessage("Block New Joins: " + block_new_joins);
        if (plugin.getManager().isForceTrigger()) {
            long remainingSeconds = plugin.getManager().getRemainingSeconds();
            sender.sendMessage("Trigger is enabled. It will be deactivated in " + remainingSeconds + " seconds.");
        } else {
            sender.sendMessage("Trigger is not currently enabled.");
        }

        sender.sendMessage("Current joins per second: " + plugin.getManager().getCurrentCount(System.currentTimeMillis() / 1000));

    }
}
