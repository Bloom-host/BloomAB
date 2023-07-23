package me.bloomab.commands.sub;

import com.velocitypowered.api.command.CommandSource;
import me.bloomab.BloomAB;
import me.bloomab.commands.interfaces.SubCommand;
import me.bloomab.managers.CounterManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ForceStopSubCommand implements SubCommand {

    private final CounterManager counterManager;

    public ForceStopSubCommand(BloomAB bloomAB) {
        this.counterManager = bloomAB.getCounterManager();
    }

    @Override
    public String getPermission() {
        return "bab.admin.manage";
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (!counterManager.isForceTrigger()) {
            sender.sendMessage(Component.text("Trigger is already stopped!", NamedTextColor.RED));
            return;
        }

        counterManager.setForceTrigger(false, 0);
        sender.sendMessage(Component.text("Trigger manually stopped.", NamedTextColor.GREEN));
    }


}
