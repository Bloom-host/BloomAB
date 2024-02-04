package host.bloom.ab.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.VelocityServer;
import com.velocitypowered.proxy.network.ConnectionManager;
import host.bloom.ab.common.utils.Utils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class VelocityLoginHookChannel extends ChannelInitializer<Channel> {

    private final VelocityPlugin plugin;
    private final ChannelInitializer original;
    private static final Method initChannel;

    static {
        initChannel = Utils.getMethod(ChannelInitializer.class, "initChannel", Channel.class);
    }

    public VelocityLoginHookChannel(VelocityPlugin plugin, ProxyServer server) throws NoSuchFieldException, IllegalAccessException {
        this.plugin = plugin;

        Class<?> velocityServerClass = VelocityServer.class;
        Field cmField = velocityServerClass.getDeclaredField("cm");
        cmField.setAccessible(true);

        // Get the connection manager
        ConnectionManager connectionManager = (ConnectionManager) cmField.get(server);
        connectionManager.getServerChannelInitializer().set(this);
        this.original = connectionManager.getServerChannelInitializer().get();
    }

    @Override
    protected void initChannel(io.netty.channel.Channel ch) {
        this.plugin.getManager().incrementConnectionCount();
        Utils.invoke(original, initChannel, ch);
    }

}
