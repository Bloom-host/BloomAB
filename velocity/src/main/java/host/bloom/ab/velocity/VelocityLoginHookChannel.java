package host.bloom.ab.velocity;

import host.bloom.ab.common.utils.Utils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

import java.lang.reflect.Method;

public class VelocityLoginHookChannel extends ChannelInitializer<Channel> {

    private final VelocityPlugin plugin;
    private final ChannelInitializer original;
    private static final Method initChannel;

    static {
        initChannel = Utils.getMethod(ChannelInitializer.class, "initChannel", Channel.class);
    }

    public VelocityLoginHookChannel(VelocityPlugin plugin, ChannelInitializer<Channel> original) {
        this.plugin = plugin;
        this.original = original;
    }

    @Override
    protected void initChannel(io.netty.channel.Channel ch) {
        this.plugin.getManager().incrementConnectionCount();
        Utils.invoke(original, initChannel, ch);
    }

}
