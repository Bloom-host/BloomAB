package host.bloom.ab.common.commands.sub;

import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.commands.Sender;
import host.bloom.ab.common.commands.SubCommand;
import host.bloom.ab.common.utils.Utils;

public class Force implements SubCommand {

    private final AbstractPlugin plugin;

    public Force(AbstractPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPermission() {
        return "bab.admin.manage";
    }

    @Override
    public void run(Sender sender, String[] args) {

        if (args.length != 2) {
            sender.sendMessage("&eUsage: /bab force &6<seconds>");
            return;
        }

        Integer seconds = Utils.getInteger(args[1]);
        if (seconds == null) {
            sender.sendMessage("&cPlease enter a valid number!");
            return;
        }

        if (seconds < 1) {
            sender.sendMessage("&cPlease enter a valid number!");
            return;
        }

        if (plugin.getManager().isForceTrigger()) {
            sender.sendMessage("&cTrigger is already enabled!");
            return;
        }

        plugin.getManager().setForceTrigger(true, seconds).exceptionally(e -> {
            sender.sendMessage("&cFailed to enable the trigger. Check the console for more details.");
            e.printStackTrace();
            return null;
        }).thenAccept(success -> {
            if (success != null) {
                sender.sendMessage("Attempting to enable the trigger...");
            }
        });

    }

}
