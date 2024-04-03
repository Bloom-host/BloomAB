package host.bloom.ab.bukkit;

import host.bloom.ab.common.commands.Sender;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitSender implements Sender {

    private final CommandSender sender;

    public BukkitSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        this.sender.sendMessage(BukkitMethods.color(message));
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.sender.hasPermission(permission);
    }

    @Override
    public void actionbar(String message) {
        if (!isPlayer()) return;

        Player player = (Player) sender;

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(BukkitMethods.color(message)));
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
