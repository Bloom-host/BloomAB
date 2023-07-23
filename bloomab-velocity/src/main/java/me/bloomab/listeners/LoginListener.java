package me.bloomab.listeners;


import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.VelocityServer;
import com.velocitypowered.proxy.connection.client.LoginInboundConnection;
import com.velocitypowered.proxy.network.ConnectionManager;
import me.bloomab.BloomAB;
import me.bloomab.managers.CounterManager;

import java.lang.reflect.Field;

public class LoginListener {

    private final CounterManager counterManager;

    public LoginListener(BloomAB instance) {
        this.counterManager = instance.getCounterManager();
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onLoginInbound(ConnectionHandshakeEvent event) {
        if (event.getConnection() instanceof LoginInboundConnection) {
            counterManager.incrementConnectionCount();
        }
    }
}
