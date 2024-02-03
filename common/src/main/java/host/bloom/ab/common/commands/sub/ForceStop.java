package host.bloom.ab.common.commands.sub;

import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.commands.Sender;
import host.bloom.ab.common.commands.SubCommand;

import java.util.Collections;

public class ForceStop implements SubCommand {

    private final AbstractPlugin plugin;

    public ForceStop(AbstractPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPermission() {
        return "bab.admin.manage";
    }

    @Override
    public void run(Sender sender, String[] args) {
        if (!plugin.getManager().isForceTrigger()) {
            sender.sendMessage("Trigger is already §cstopped§f!");
            return;
        }

        plugin.getManager().setForceTrigger(false, 0);
        sender.sendMessage("Trigger manually §cstopped§f.");
    }

    @Override
    public Iterable<String> getTabCompletion(String[] args) {
        if (args.length == 2) return Collections.singletonList("<seconds>");
        return Collections.emptyList();
    }

}
