package me.bloomab.commands;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.google.common.collect.Maps;
import com.velocitypowered.api.command.SimpleCommand;
import me.bloomab.BloomAB;
import me.bloomab.commands.interfaces.SubCommand;
import me.bloomab.commands.sub.ForceStopSubCommand;
import me.bloomab.commands.sub.ForceSubCommand;
import me.bloomab.commands.sub.SetSubCommand;
import me.bloomab.managers.CounterManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.*;

public class CommandManager implements SimpleCommand {

    private final CounterManager counterManager;
    private final Map<String, SubCommand> commands = Maps.newHashMap();

    public CommandManager(BloomAB bloomAB) {
        this.counterManager = bloomAB.getCounterManager();

        commands.put("forcestop", new ForceStopSubCommand(bloomAB));
        commands.put("force", new ForceSubCommand(bloomAB));
        commands.put("set", new SetSubCommand(bloomAB));
    }


    @Override
    public List<String> suggest(final Invocation invocation) {
        CommandSource commandSender = invocation.source();
        String[] strings = invocation.arguments();
        if (!commandSender.hasPermission("bab.admin")) {
            return Collections.emptyList();
        }

        if (strings.length == 1) {
            return new ArrayList<>(commands.keySet());
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

    private void sendHelpMessage(CommandSource sender) {
        sender.sendMessage(Component.text("=========", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("BloomAB Commands", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("=========", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/bab force <seconds>: Enable force trigger for <seconds> seconds.", NamedTextColor.GREEN));
        sender.sendMessage(Component.text("/bab forcestop: Force stop the trigger and keep it disabled until the finish of the attack.", NamedTextColor.GREEN));
        sender.sendMessage(Component.text("/bab set maxjps <number>: Set max joins per second.", NamedTextColor.GREEN));
        sender.sendMessage(Component.text("/bab set duration <seconds>: Set trigger duration.", NamedTextColor.GREEN));
        sender.sendMessage(Component.text("=========", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("Connections stats", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("=========", NamedTextColor.GOLD));
        int maxRPS = counterManager.getMaxJoinsPerSecond();
        int triggerDuration = counterManager.getTriggerDuration();
        String block_new_joins = counterManager.getBlockNewJoins();
        sender.sendMessage(Component.text("Trigger joins Per Second: " + maxRPS + "rps", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("Trigger Duration: " + triggerDuration + " seconds", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("Block New Joins: " + block_new_joins, NamedTextColor.YELLOW));
        if (counterManager.isForceTrigger()) {
            long remainingSeconds = counterManager.getRemainingSeconds();
            sender.sendMessage(Component.text("Trigger is enabled. It will be deactivated in " + remainingSeconds + " seconds.", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("Trigger is not currently enabled.", NamedTextColor.RED));
        }

        long currentTime = System.currentTimeMillis() / 1000;
        int currentCount = counterManager.getCurrentCount(currentTime);
        sender.sendMessage(Component.text("Current joins per second: " + currentCount, NamedTextColor.YELLOW));
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource commandSender = invocation.source();

        if (!commandSender.hasPermission("bab.admin")) {
            commandSender.sendMessage(Component.text("You don't have enough permissions to execute this command!", NamedTextColor.RED));
            return;
        }

        if (invocation.arguments().length == 0) {
            sendHelpMessage(commandSender);
            return;
        }

        SubCommand subCommand = commands.get(invocation.arguments()[0].toLowerCase());

        if (subCommand == null || !commandSender.hasPermission(subCommand.getPermission())) {
            commandSender.sendMessage(Component.text("You don't have enough permissions to execute this command!", NamedTextColor.RED));
            return;
        }

        try {
            subCommand.run(commandSender, invocation.arguments());
        } catch (Exception e) {
            commandSender.sendMessage(Component.text("Failed to execute command: " + e.getMessage(), NamedTextColor.RED));
        }
    }
}
