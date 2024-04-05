package host.bloom.ab.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import host.bloom.ab.common.commands.Sender;
import java.util.UUID;
import host.bloom.ab.common.utils.Utils;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class VelocitySender implements Sender {

    private final CommandSource source;

    public VelocitySender(CommandSource source) {
        this.source = source;
    }

    @Override
    public void sendMessage(String message) {
        this.source.sendMessage(MiniMessage.miniMessage().deserialize(message));
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.source.hasPermission(permission);
    }

    @Override
    public void actionbar(String message) {
        if (!isPlayer()) return;

        Player player = (Player) this.source;

        player.sendActionBar(Utils.color(message));
    }

    @Override
    public boolean isPlayer() {
        return this.source instanceof Player;
    }

    @Override
    public UUID getUUID() {
        if (!isPlayer()) return null;

        Player player = (Player) this.source;

        return player.getUniqueId();
    }
}