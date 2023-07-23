package me.bloomab.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import lombok.SneakyThrows;
import me.bloomab.BloomAB;
import me.bloomab.commands.interfaces.SubCommand;
import me.bloomab.configs.MainConfiguration;
import me.bloomab.managers.CounterManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class SetSubCommand implements SubCommand {

    private final BloomAB bloomAB;
    private final CounterManager counterManager;

    public SetSubCommand(BloomAB bloomAB) {
        this.bloomAB = bloomAB;
        this.counterManager = bloomAB.getCounterManager();
    }

    @Override
    public String getPermission() {
        return "bab.admin";
    }

    @Override
    @SneakyThrows
    public void run(CommandSource sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /bab set duration / maxjps <value>", NamedTextColor.RED));
            return;
        }

        switch (args[1]) {
            case "duration":
                if (args.length < 3) {
                    sender.sendMessage(Component.text("Usage: /bab set duration <seconds>", NamedTextColor.RED));
                    return;
                }

                int duration = Integer.parseInt(args[2]);

                if (duration > 3600) {
                    sender.sendMessage(Component.text("Trigger duration exceeds the maximum allowed value (3600 seconds).", NamedTextColor.RED));
                    return;
                }

                counterManager.setTriggerDuration(duration);
                bloomAB.getConfiguration().setProperty(MainConfiguration.TRIGGER_DURATION, duration);
                bloomAB.getConfiguration().save();
                sender.sendMessage(Component.text("Trigger duration set to " + duration + " seconds!", NamedTextColor.GREEN));
                break;
            case "maxjps":
                if (args.length < 3) {
                    sender.sendMessage(Component.text("Usage: /bab set maxjps <value>", NamedTextColor.RED));
                    return;
                }

                int maxJps = Integer.parseInt(args[2]);

                if (maxJps > 100000) {
                    sender.sendMessage(Component.text("Max joins per second exceeds the maximum allowed value (100000).", NamedTextColor.RED));
                    return;
                }

                counterManager.setMaxJoinsPerSecond(maxJps);
                bloomAB.getConfiguration().setProperty(MainConfiguration.MAX_JOINS_PER_SECOND, maxJps);
                bloomAB.getConfiguration().save();
                sender.sendMessage(Component.text("Max joins per second set to " + maxJps + "!", NamedTextColor.GREEN));
                break;
            default:
                sender.sendMessage(Component.text("Usage: /bab set duration / maxjps <value>", NamedTextColor.RED));
        }
    }
}
