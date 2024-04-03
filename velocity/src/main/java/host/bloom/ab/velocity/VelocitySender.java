package host.bloom.ab.velocity;

import com.velocitypowered.api.command.CommandSource;
import host.bloom.ab.common.commands.Sender;
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
}