package host.bloom.ab.common.commands.sub;

import host.bloom.ab.common.AbstractPlugin;
import host.bloom.ab.common.commands.Sender;
import host.bloom.ab.common.commands.SubCommand;
import host.bloom.ab.common.config.enums.Messages;
import host.bloom.ab.common.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            sender.sendMessage(Messages.invalid_usage.getMessage("{usage}", "/bab force [seconds]"));
            return;
        }

        Integer seconds = Utils.getInteger(args[1]);

        if (seconds == null || seconds < 1) {
            sender.sendMessage(Messages.invalid_number.getMessage());
            return;
        }

        if (plugin.getManager().isForceTrigger()) {
            sender.sendMessage(Messages.trigger_enabled.getMessage());
            return;
        }

        plugin.getManager().setForceTrigger(true, seconds).exceptionally(e -> {
            sender.sendMessage(Messages.trigger_failed.getMessage());
            e.printStackTrace();
            return null;
        }).thenAccept(success -> {
            if (success != null) {
                sender.sendMessage(Messages.trigger_attempting.getMessage());
            }
        });
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        if (args.length == 2) {
            List<String> numbers = new ArrayList<>();

            for (int amount = 1; amount < 60; amount++) numbers.add(String.valueOf(amount));

            return numbers;
        }

        return Collections.emptyList();
    }
}