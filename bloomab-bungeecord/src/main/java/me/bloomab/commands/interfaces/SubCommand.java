package me.bloomab.commands.interfaces;

import net.md_5.bungee.api.CommandSender;

public interface SubCommand {

    String getPermission();

    void run(CommandSender sender, String[] args);

}
