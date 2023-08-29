package me.bloomab.commands.sub;

import me.bloomab.BloomAB;
import me.bloomab.commands.interfaces.SubCommand;
import me.bloomab.managers.CounterManager;
import me.bloomab.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class SetSubCommand implements SubCommand {

    private final BloomAB BloomAB;
    private final CounterManager counterManager;

    public SetSubCommand(BloomAB lowHostingAB) {
        this.BloomAB = lowHostingAB;
        this.counterManager = lowHostingAB.getCounterManager();
    }

    @Override
    public String getPermission() {
        return "bab.admin";
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new TextComponent("§cUsage: /bab set duration / maxjps <value>"));
            return;
        }

        switch (args[1]) {
            case "duration":
                int duration = Integer.parseInt(args[2]);

                if (duration > 3600) {
                    sender.sendMessage(new TextComponent("§cTrigger duration exceeds the maximum allowed value (3600 seconds)."));
                    return;
                }
                if (args.length < 3) {
                    sender.sendMessage(new TextComponent("§cUsage: /bab set duration <seconds>"));
                    return;
                }

                if (args[2].isEmpty() || args[2].equals("0") || !Utils.checkIfInteger(args[2])) {
                    sender.sendMessage(new TextComponent("§cPlease enter a valid number!"));
                    return;
                }

                counterManager.setTriggerDuration(duration);
                BloomAB.getConfigFile().getConfig().set("trigger_duration", duration);
                BloomAB.saveConfiguration();
                sender.sendMessage(new TextComponent("Trigger duration set to " + duration + " seconds!"));
                break;
            case "maxjps":
                int maxJps = Integer.parseInt(args[2]);

                if (maxJps > 100000) {
                    sender.sendMessage(new TextComponent("§cMax joins Per Second exceeds the maximum allowed value (100000)."));
                    return;
                }
                if (args.length < 3) {
                    sender.sendMessage(new TextComponent("§cUsage: /bab set maxjps <seconds>"));
                    return;
                }

                if (args[2].isEmpty() || args[2].equals("0") || !Utils.checkIfInteger(args[2])) {
                    sender.sendMessage(new TextComponent("§cPlease enter a valid number!"));
                    return; // domanda ma se hai tolto il get
                }

                counterManager.setMaxJoinsPerSecond(maxJps);
                BloomAB.getConfigFile().getConfig().set("max_joins_per_second", maxJps);
                BloomAB.saveConfiguration();
                sender.sendMessage(new TextComponent("Max joins per second set to " + maxJps + "!"));
                break;
            default:
                sender.sendMessage(new TextComponent("§cUsage: /bab set duration / maxjps <seconds>"));
        }
    }
}
