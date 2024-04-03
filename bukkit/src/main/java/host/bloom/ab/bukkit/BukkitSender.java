package host.bloom.ab.bukkit;

import host.bloom.ab.common.commands.Sender;
import host.bloom.ab.common.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BukkitSender implements Sender {

    private final @NotNull BukkitPlugin plugin = JavaPlugin.getPlugin(BukkitPlugin.class);

    private final CommandSender sender;

    public BukkitSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        this.plugin.adventure().sender(this.sender).sendMessage(Utils.color(message));
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.sender.hasPermission(permission);
    }
}