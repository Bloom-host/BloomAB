package host.bloom.ab.bukkit;

import host.bloom.ab.common.commands.Sender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.UUID;
import host.bloom.ab.common.utils.Utils;

public class BukkitSender implements Sender {

    private final BukkitPlugin plugin;
    private final CommandSender sender;

    public BukkitSender(BukkitPlugin plugin, CommandSender sender) {
        this.plugin = plugin;
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

    @Override
    public void actionbar(String message) {
        if (!isPlayer()) return;

        Player player = (Player) sender;

        this.plugin.adventure().sender(player).sendActionBar(Utils.color(message));
    }

    @Override
    public boolean isPlayer() {
        return this.sender instanceof Player;
    }

    @Override
    public UUID getUUID() {
        if (!isPlayer()) return null;

        Player player = (Player) sender;

        return player.getUniqueId();
    }
}