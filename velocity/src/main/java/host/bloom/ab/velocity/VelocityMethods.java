package host.bloom.ab.velocity;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class VelocityMethods {

    public static TextComponent color(String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }
}