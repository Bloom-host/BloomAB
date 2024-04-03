package host.bloom.ab.common.commands.sub;

import host.bloom.ab.common.commands.Sender;
import host.bloom.ab.common.commands.SubCommand;
import host.bloom.ab.common.config.enums.Messages;
import host.bloom.ab.common.managers.ConfigManager;

public class Reload implements SubCommand {

    @Override
    public String getPermission() {
        return "bab.reload";
    }

    @Override
    public void run(Sender sender, String[] args) {
        ConfigManager.reload();

        sender.sendMessage(Messages.reloaded_plugin.getMessage());
    }
}