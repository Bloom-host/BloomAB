package host.bloom.ab.common.commands.sub;

import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.commands.Sender;
import host.bloom.ab.common.commands.SubCommand;
import host.bloom.ab.common.config.enums.Messages;
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
        if (this.counterManager.containsSeer(sender.getUUID())) {
            this.counterManager.removeSeer(sender.getUUID());

            sender.sendMessage(Messages.action_bar_disabled.getMessage());

            return;
        }

        this.counterManager.addSeer(sender.getUUID());

        sender.sendMessage(Messages.action_bar_enabled.getMessage());
    }
}