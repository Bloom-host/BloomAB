package host.bloom.ab.bukkit;

import host.bloom.ab.common.commands.Sender;
import org.bukkit.command.CommandSender;

public class BukkitSender implements Sender {

    private final CommandSender sender;

    public BukkitSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        this.sender.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.sender.hasPermission(permission);
    }

}
