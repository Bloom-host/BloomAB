package host.bloom.ab.waterfall;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class WaterfallMethods {

    public static BaseComponent[] color(String message) {
        Component component = MiniMessage.miniMessage().deserialize(message);

        String json = GsonComponentSerializer.gson().serialize(component);

        return ComponentSerializer.parse(json);
    }
}