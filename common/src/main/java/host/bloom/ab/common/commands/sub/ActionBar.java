package host.bloom.ab.common.commands.sub;

import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.commands.Sender;
import host.bloom.ab.common.commands.SubCommand;
import host.bloom.ab.common.managers.CounterManager;

public class ActionBar implements SubCommand {

    private final CounterManager counterManager;

    public ActionBar(AbstractPlugin plugin) {
        this.counterManager = plugin.getManager();
    }

    @Override
    public String getPermission() {
        return "bab.admin.actionbar";
    }

    @Override
    public void run(Sender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("&eUsage: /bab actionbar");
            return;
        }

        if (this.counterManager.containsSeer(sender.getUUID())) {
            this.counterManager.removeSeer(sender.getUUID());

            sender.sendMessage("&eYou are no longer receiving notifications.");

            return;
        }

        this.counterManager.addSeer(sender.getUUID());

        sender.sendMessage("&eYou are now receiving notifications.");
    }
}