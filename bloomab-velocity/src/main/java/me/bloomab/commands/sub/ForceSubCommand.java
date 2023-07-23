package me.bloomab.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import me.bloomab.BloomAB;
import me.bloomab.commands.interfaces.SubCommand;
import me.bloomab.managers.CounterManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ForceSubCommand implements SubCommand {

    private final CounterManager counterManager;

    public ForceSubCommand(BloomAB bloomAB) {
        this.counterManager = bloomAB.getCounterManager();
    }

    @Override
    public String getPermission() {
        return "bab.admin.manage";
    }

    @Override
    public void run(CommandSource sender, String[] args) {

        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /bab force <seconds>", NamedTextColor.RED));
            return;
        }

        int seconds = Integer.parseInt(args[1]);

        if (seconds < 1) {
            sender.sendMessage(Component.text("Please enter a valid number!", NamedTextColor.RED));
            return;
        }

        if (counterManager.isForceTrigger()) {
            sender.sendMessage(Component.text("Trigger is already enabled!", NamedTextColor.GREEN));
            return;
        }

        counterManager.setForceTrigger(true, seconds)
                .exceptionally(e -> {
                    e.printStackTrace();
                    sender.sendMessage(Component.text("§cFailed to enable the trigger. Check the console for more details."));
                    return null;
                })
                .thenAccept(success -> {
                    if (success != null) {
                        sender.sendMessage(Component.text("Attempting to enable the trigger..."));
                    }
                });
        sender.sendMessage(Component.text("Attempting to enable the trigger...", NamedTextColor.YELLOW));
    }
}
