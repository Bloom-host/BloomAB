package host.bloom.ab.waterfall;

import host.bloom.ab.common.managers.CounterManager;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class WaterfallQuitListener implements Listener {

    private final CounterManager manager;

    public WaterfallQuitListener(CounterManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        this.manager.removeSeer(event.getPlayer().getUniqueId());
    }
}
