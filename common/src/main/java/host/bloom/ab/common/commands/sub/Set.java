package host.bloom.ab.common.commands.sub;

import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.commands.Sender;
import host.bloom.ab.common.commands.SubCommand;
import host.bloom.ab.common.config.Location;
import host.bloom.ab.common.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
            sender.sendMessage("§cUsage: /bab set <duration/maxjps/location> <value>");
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
                    return;
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
                    return;
                }

                sender.sendMessage("Max joins per second set to " + maxJps + "!");
                break;

            case "location":
                if (args.length < 3 || args[2].isEmpty()) {
                    sender.sendMessage("§cUsage: /bab set location <location>");
                    return;
                }

                Location location;
                try {
                    location = Location.valueOf(args[2]);
                } catch (IllegalArgumentException exception) {
                    sender.sendMessage("§cInvalid location§f: §4" + args[2]);
                    return;
                }

                plugin.getABConfig().location = location;
                try {
                    plugin.getABConfig().save();
                } catch (IOException exception) {
                    sender.sendMessage("§cUnable to save config§f: §4" + exception.getMessage());
                    return;
                }

                sender.sendMessage("§eSet location to§f: §6" + location.getDisplayName());
                break;

            default:
                sender.sendMessage("§cUsage: /bab set <duration/maxjps/location> <value>");
        }
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        if (args.length == 2) {
            return Arrays.asList("maxjps", "duration", "location");
        }

        if (args.length == 3) {
            switch (args[1]) {
                case "maxjps":
                case "duration": {
                    return Collections.singletonList("<number>");
                }

                case "location": {
                    List<String> locations = new ArrayList<>();
                    for (Location location : Location.values()) locations.add(location.name());
                    return locations;
                }
            }
        }

        return Collections.emptyList();
    }

}
