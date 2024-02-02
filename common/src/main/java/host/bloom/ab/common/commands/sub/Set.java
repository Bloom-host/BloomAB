package host.bloom.ab.common.commands.sub;

import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.commands.Sender;
import host.bloom.ab.common.commands.SubCommand;
import host.bloom.ab.common.utils.Utils;

import java.io.IOException;
import java.util.Collections;

public class Set implements SubCommand {

    private final AbstractPlugin plugin;

    public Set(AbstractPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPermission() {
        return "bab.admin";
    }

    @Override
    public void run(Sender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /bab set duration / maxjps <value>");
            return;
        }

        switch (args[1]) {
            case "duration":
                int duration = Integer.parseInt(args[2]);

                if (duration > 3600) {
                    sender.sendMessage("§cTrigger duration exceeds the maximum allowed value (3600 seconds).");
                    return;
                }
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /bab set duration <seconds>");
                    return;
                }

                if (args[2].isEmpty() || args[2].equals("0") || !Utils.isInteger(args[2])) {
                    sender.sendMessage("§cPlease enter a valid number!");
                    return;
                }

                plugin.getConfig().triggerDuration = duration;
                try {
                    plugin.getConfig().save();
                } catch (IOException exception) {
                    sender.sendMessage("§cUnable to save config: " + exception.getMessage());
                }

                sender.sendMessage("Trigger duration set to " + duration + " seconds!");
                break;

            case "maxjps":
                int maxJps = Integer.parseInt(args[2]);

                if (maxJps > 100000) {
                    sender.sendMessage("§cMax joins Per Second exceeds the maximum allowed value (100000).");
                    return;
                }

                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /bab set maxjps <seconds>");
                    return;
                }

                if (args[2].isEmpty() || args[2].equals("0") || !Utils.isInteger(args[2])) {
                    sender.sendMessage("§cPlease enter a valid number!");
                    return; // domanda ma se hai tolto il get
                }

                plugin.getConfig().maxJoinsPerSecond = maxJps;
                try {
                    plugin.getConfig().save();
                } catch (IOException exception) {
                    sender.sendMessage("§cUnable to save config: " + exception.getMessage());
                }

                sender.sendMessage("Max joins per second set to " + maxJps + "!");
                break;
            default:
                sender.sendMessage("§cUsage: /bab set duration / maxjps <seconds>");
        }
    }

    @Override
    public Iterable<String> getTabCompletion(String[] args) {
        if (args.length == 2) {
            return Collections.singletonList("<maxjps|duration>");
        }

        if (args.length == 3) {
            return Collections.singletonList("<number>");
        }
        return null;
    }

}
