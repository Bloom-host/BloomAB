package host.bloom.ab.common.commands.sub;

import ch.jalu.configme.SettingsManager;
import host.bloom.ab.common.commands.Sender;
import host.bloom.ab.common.commands.SubCommand;
import host.bloom.ab.common.config.ConfigKeys;
import host.bloom.ab.common.config.enums.Location;
import host.bloom.ab.common.config.enums.Messages;
import host.bloom.ab.common.managers.ConfigManager;
import host.bloom.ab.common.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Set implements SubCommand {

    public Set() {}

    @Override
    public String getPermission() {
        return "bab.admin";
    }

    @Override
    public void run(Sender sender, String[] args) {
        SettingsManager config = ConfigManager.getConfig();

        if (args.length < 2) {
            sender.sendMessage(Messages.invalid_usage.getMessage("{usage}", "/bab set [duration/maxjps/location] [value]"));
            return;
        }

        switch (args[1]) {
            case "duration":
                if (args.length < 3) {
                    sender.sendMessage(Messages.invalid_usage.getMessage("{usage}", "/bab set duration [seconds]"));
                    return;
                }

                Integer duration = Utils.getInteger(args[2]);
                if (duration == null) {
                    sender.sendMessage(Messages.invalid_number.getMessage());
                    return;
                }

                if (duration > 3600) {
                    sender.sendMessage(Messages.trigger_exceeds_duration.getMessage());
                    return;
                }

                config.setProperty(ConfigKeys.trigger_duration, duration);
                config.save();
                config.reload();

                sender.sendMessage(Messages.trigger_set_duration.getMessage("{duration}", String.valueOf(config.getProperty(ConfigKeys.trigger_duration))));
                break;

            case "maxjps":
                if (args.length < 3) {
                    sender.sendMessage(Messages.invalid_usage.getMessage("{usage}", "/bab set maxjps [seconds]"));
                    return;
                }

                Integer maxJps = Utils.getInteger(args[2]);
                if (maxJps == null || maxJps == 0) {
                    sender.sendMessage(Messages.invalid_number.getMessage());
                    return;
                }

                if (maxJps > 100000) {
                    sender.sendMessage(Messages.exceeds_max_joins.getMessage());
                    return;
                }

                config.setProperty(ConfigKeys.max_joins_per_second, maxJps);
                config.save();
                config.reload();

                sender.sendMessage(Messages.max_join_set.getMessage("{value}", String.valueOf(config.getProperty(ConfigKeys.max_joins_per_second))));
                break;

            case "location":
                if (args.length < 3 || args[2].isEmpty()) {
                    sender.sendMessage(Messages.invalid_usage.getMessage("{usage}", "/bab set location [location]"));
                    return;
                }

                Location location;
                try {
                    location = Location.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException exception) {
                    sender.sendMessage(Messages.invalid_location.getMessage("{location}", args[2]));
                    return;
                }

                config.setProperty(ConfigKeys.locations, location);
                config.save();
                config.reload();

                sender.sendMessage(Messages.set_location.getMessage("{location}", config.getProperty(ConfigKeys.locations).getDisplayName()));
                break;

            default:
                sender.sendMessage(Messages.invalid_usage.getMessage("{usage}", "/bab set [duration/maxjps/location] [value]"));
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
                    List<String> numbers = new ArrayList<>();

                    for (int amount = 1; amount < 60; amount++) numbers.add(String.valueOf(amount));

                    return numbers;
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
