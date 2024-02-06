package host.bloom.ab.common.commands.sub;

import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.commands.Sender;
import host.bloom.ab.common.commands.SubCommand;
import host.bloom.ab.common.utils.Utils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

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
            sender.sendMessage("§cUsage: /bab set <duration/maxjps> <value>");
            return;
        }

        switch (args[1]) {
            case "duration":
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /bab set duration <seconds>");
                    return;
                }

                Integer duration = Utils.getInteger(args[2]);
                if (duration == null) {
                    sender.sendMessage("§cPlease enter a valid number!");
                    return;
                }

                if (duration > 3600) {
                    sender.sendMessage("§cTrigger duration exceeds the maximum allowed value (3600 seconds).");
                    return;
                }

                plugin.getABConfig().triggerDuration = duration;
                try {
                    plugin.getABConfig().save();
                } catch (IOException exception) {
                    sender.sendMessage("§cUnable to save config: " + exception.getMessage());
                }

                sender.sendMessage("Trigger duration set to " + duration + " seconds!");
                break;

            case "maxjps":
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /bab set maxjps <seconds>");
                    return;
                }

                Integer maxJps = Utils.getInteger(args[2]);
                if (maxJps == null || maxJps == 0) {
                    sender.sendMessage("§cPlease enter a valid number!");
                    return;
                }

                if (maxJps > 100000) {
                    sender.sendMessage("§cMax joins per second exceeds the maximum allowed value (100000).");
                    return;
                }

                plugin.getABConfig().maxJoinsPerSecond = maxJps;
                try {
                    plugin.getABConfig().save();
                } catch (IOException exception) {
                    sender.sendMessage("§cUnable to save config: " + exception.getMessage());
                }

                sender.sendMessage("Max joins per second set to " + maxJps + "!");
                break;
            default:
                sender.sendMessage("§cUsage: /bab set <duration/maxjps> <seconds>");
        }
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        if (args.length == 2) {
            return List.of("maxjps", "duration");
        }

        if (args.length == 3) {
            return Collections.singletonList("<number>");
        }

        return Collections.emptyList();
    }

}
