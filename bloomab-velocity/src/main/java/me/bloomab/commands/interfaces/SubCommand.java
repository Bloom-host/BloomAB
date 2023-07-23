package me.bloomab.commands.interfaces;

import com.velocitypowered.api.command.CommandSource;

public interface SubCommand {

    String getPermission();

    void run(CommandSource sender, String[] args);

}