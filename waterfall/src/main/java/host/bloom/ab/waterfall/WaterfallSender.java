package host.bloom.ab.waterfall;

import host.bloom.ab.common.commands.Sender;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class WaterfallSender implements Sender {

    private final CommandSender sender;

    public WaterfallSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        this.sender.sendMessage(new TextComponent(WaterfallMethods.color(message)));
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.sender.hasPermission(permission);
    }

    @Override
    public void actionbar(String message) {
        if (!isPlayer()) return;

        ProxiedPlayer player = (ProxiedPlayer) this.sender;

        player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(WaterfallMethods.color(message)));
    }

    @Override
    public boolean isPlayer() {
        return this.sender instanceof ProxiedPlayer;
    }

    @Override
    public UUID getUUID() {
        if (!isPlayer()) return null;

        ProxiedPlayer player = (ProxiedPlayer) this.sender;

        return player.getUniqueId();
    }
}
