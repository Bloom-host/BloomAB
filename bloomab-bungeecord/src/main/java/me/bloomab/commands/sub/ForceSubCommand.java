package me.bloomab.commands.sub;

import me.bloomab.BloomAB;
import me.bloomab.commands.interfaces.SubCommand;
import me.bloomab.managers.CounterManager;
import me.bloomab.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.concurrent.ExecutionException;

public class ForceSubCommand implements SubCommand {

    private final CounterManager counterManager;

    public ForceSubCommand(BloomAB BloomAB) {
        this.counterManager = BloomAB.getCounterManager();
    }

    @Override
    public String getPermission() {
        return "bab.admin.manage";
    }

    @Override
    public void run(CommandSender sender, String[] args) {

        if (args.length != 2) {
            sender.sendMessage(new TextComponent("§cUsage: /BloomAB force <seconds>"));
            return;
        }

        boolean isNumber = Utils.checkIfInteger(args[1]);

        if (!isNumber) {
            sender.sendMessage(new TextComponent("§cPlease enter a valid number!"));
            return;
        }

        int seconds = Integer.parseInt(args[1]);

        if (seconds < 1) {
            sender.sendMessage(new TextComponent("§cPlease enter a valid number!"));
            return;
        }
        if (counterManager.isForceTrigger()) {
            sender.sendMessage(new TextComponent("Trigger is already §aenabled§7!"));
            return;
        }

        counterManager.setForceTrigger(true, seconds)
                .exceptionally(e -> {
                    e.printStackTrace();
                    sender.sendMessage(new TextComponent("§cFailed to enable the trigger. Check the console for more details."));
                    return null;
                })
                .thenAccept(success -> {
                    if (success != null) {
                        sender.sendMessage(new TextComponent("Attempting to enable the trigger..."));
                    }
                });

    }

    }

