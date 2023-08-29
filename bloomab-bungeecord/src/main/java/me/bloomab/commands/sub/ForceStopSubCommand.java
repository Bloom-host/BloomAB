package me.bloomab.commands.sub;

import me.bloomab.BloomAB;
import me.bloomab.commands.interfaces.SubCommand;
import me.bloomab.managers.CounterManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class ForceStopSubCommand implements SubCommand {

    private final CounterManager counterManager;

    public ForceStopSubCommand(BloomAB BloomAB) {
        this.counterManager = BloomAB.getCounterManager();
    }

    @Override
    public String getPermission() {
        return "bab.admin.manage";
    }

    @Override
    public void run(CommandSender sender, String[] args) {

        if (!counterManager.isForceTrigger()) {
            sender.sendMessage(new TextComponent("Trigger is already §cstopped§f!"));
            return;
        }

        counterManager.setForceTrigger(false, 0);
        sender.sendMessage(new TextComponent("Trigger manually §cstopped§f."));
    }
}
