package me.bloomab.listeners;

import me.bloomab.BloomAB;
import me.bloomab.managers.CounterManager;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;

public class LoginListener implements Listener {

    private final CounterManager counterManager;

    public LoginListener(BloomAB BloomAB) {
        this.counterManager = BloomAB.getCounterManager();
    }

    @EventHandler(priority = Byte.MIN_VALUE)
    public void onHandshake(PlayerHandshakeEvent event) {
        if(event.getConnection() instanceof InitialHandler) {
            counterManager.incrementConnectionCount();
        }
    }

}
