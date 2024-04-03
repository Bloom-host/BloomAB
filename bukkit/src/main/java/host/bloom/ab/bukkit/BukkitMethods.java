package host.bloom.ab.bukkit;

import net.md_5.bungee.api.ChatColor;

public class BukkitMethods {

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
