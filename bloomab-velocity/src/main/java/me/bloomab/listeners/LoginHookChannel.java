package me.bloomab.listeners;

import com.velocitypowered.proxy.VelocityServer;
import com.velocitypowered.proxy.network.ServerChannelInitializer;
import me.bloomab.managers.CounterManager;

public class LoginHookChannel extends ServerChannelInitializer {

    private final CounterManager counterManager;

    public LoginHookChannel(VelocityServer server, CounterManager counterManager) {
        super(server);
        this.counterManager = counterManager;
    }

    @Override
    protected void initChannel(io.netty.channel.Channel ch) {
        counterManager.incrementConnectionCount();
        super.initChannel(ch);
    }
}
