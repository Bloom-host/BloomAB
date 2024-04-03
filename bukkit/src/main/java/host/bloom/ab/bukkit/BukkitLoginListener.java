package host.bloom.ab.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class BukkitLoginListener implements Listener {

    private final BukkitPlugin plugin;

    public BukkitLoginListener(BukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        plugin.getManager().incrementConnectionCount();
    }

    @EventHandler
    public void onServerListPingEvent(ServerListPingEvent e) {
        plugin.getManager().incrementConnectionCount();
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        this.plugin.getManager().removeSeer(event.getPlayer().getUniqueId());
    }
}
