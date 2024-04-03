package host.bloom.ab.waterfall;

import host.bloom.ab.common.managers.CounterManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class WaterfallLoginListener implements Listener {

    private final CounterManager manager;

    public WaterfallLoginListener(CounterManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (player.hasPermission("bab.admin.actionbar")) {
            this.manager.addSeer(player.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        this.manager.removeSeer(event.getPlayer().getUniqueId());
    }
}
