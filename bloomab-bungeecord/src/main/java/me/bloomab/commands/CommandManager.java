package me.bloomab.commands;

import com.google.common.collect.Maps;
import me.bloomab.BloomAB;
import me.bloomab.commands.interfaces.SubCommand;
import me.bloomab.commands.sub.ForceStopSubCommand;
import me.bloomab.commands.sub.ForceSubCommand;
import me.bloomab.commands.sub.SetSubCommand;
import me.bloomab.managers.CounterManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Collections;
import java.util.HashMap;

public class CommandManager extends Command implements TabExecutor {

    private final CounterManager counterManager;
    private final HashMap<String, SubCommand> commands = Maps.newHashMap();

    public CommandManager(BloomAB BloomAB) {
        super("bloomab", null, "bab", "blab");
        this.counterManager = BloomAB.getCounterManager();

        commands.put("forcestop", new ForceStopSubCommand(BloomAB));
        commands.put("force", new ForceSubCommand(BloomAB));
        commands.put("set", new SetSubCommand(BloomAB));
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

        if (!commandSender.hasPermission("bab.admin")) {
            commandSender.sendMessage(new TextComponent("§cYou don't have enough permissions to execute this command!"));
            return;
        }

        if (strings.length == 0) {
            sendHelpMessage(commandSender);
            return;
        }

        if (!commands.containsKey(strings[0].toLowerCase())) {
            sendHelpMessage(commandSender);
            return;
        }

        SubCommand subCommand = commands.get(strings[0].toLowerCase());

        if (!commandSender.hasPermission(subCommand.getPermission())) {
            commandSender.sendMessage(new TextComponent("§cYou don't have enough permissions to execute this command!"));
            return;
        }

        try {
            subCommand.run(commandSender, strings);
        } catch (Exception e) {
            commandSender.sendMessage(new TextComponent("Failed to execute command: " + e.getMessage()));
        }
    }


    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {

        if (!commandSender.hasPermission("bab.admin")) {
            return Collections.emptyList();
        }

        if (strings.length == 1) {
            return commands.keySet();
        }

        switch (strings[0].toLowerCase()) {
            case "force":
                if (strings.length == 2) {
                    return Collections.singletonList("<seconds>");
                }
                break;
            case "set":
                if (strings.length == 2) {
                    return Collections.singletonList("<maxjps|duration>");
                }
                if (strings.length == 3) {
                    return Collections.singletonList("<number>");
                }
                break;
        }

        return Collections.emptyList();
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(new TextComponent("========="));
        sender.sendMessage(new TextComponent("BloomAB Commands"));
        sender.sendMessage(new TextComponent("========="));
        sender.sendMessage(new TextComponent("/bab force <seconds>: Enable force trigger for <seconds> seconds."));
        sender.sendMessage(new TextComponent("/bab forcestop: Force stop the trigger and keep it disabled until the finish of the attack."));
        sender.sendMessage(new TextComponent("/bab set maxjps <number>: Set max joins per second."));
        sender.sendMessage(new TextComponent("/bab set duration <seconds>: Set trigger duration."));
        sender.sendMessage(new TextComponent("========="));
        sender.sendMessage(new TextComponent("Connections stats"));
        sender.sendMessage(new TextComponent("========="));
        int maxRPS = counterManager.getMaxJoinsPerSecond();
        int triggerDuration = counterManager.getTriggerDuration();
        String block_new_joins = counterManager.getBlockNewJoins(); //thanks <3
        sender.sendMessage(new TextComponent("Trigger joins Per Second: " + maxRPS + "rps"));
        sender.sendMessage(new TextComponent("Trigger Duration: " + triggerDuration + " seconds"));
        sender.sendMessage(new TextComponent("Block New Joins: " + block_new_joins));
        if (counterManager.isForceTrigger()) {
            long remainingSeconds = counterManager.getRemainingSeconds();
            sender.sendMessage(new TextComponent("Trigger is enabled. It will be deactivated in " + remainingSeconds + " seconds."));
        } else {
            sender.sendMessage(new TextComponent("Trigger is not currently enabled."));
        }

        long currentTime = System.currentTimeMillis() / 1000;
        int currentCount = counterManager.getCurrentCount(currentTime);
        sender.sendMessage(new TextComponent("Current joins per second: " + currentCount));

    }
}
