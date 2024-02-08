package host.bloom.ab.waterfall;

import host.bloom.ab.common.managers.CounterManager;
import host.bloom.ab.common.utils.Utils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import net.md_5.bungee.netty.PipelineUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class WaterfallLoginHookChannel extends ChannelInitializer<Channel> {

    private final CounterManager manager;
    private final ChannelInitializer<Channel> oldChildHandler = PipelineUtils.SERVER_CHILD;
    private static final long SERVER_CHILD_OFFSET;
    private static final Method initChannel;

    static {
        initChannel = Utils.getMethod(ChannelInitializer.class, "initChannel", Channel.class);

        try {
            Field childField = PipelineUtils.class.getDeclaredField("SERVER_CHILD");
            SERVER_CHILD_OFFSET = UnsafeAccess.UNSAFE.staticFieldOffset(childField);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public WaterfallLoginHookChannel(CounterManager manager) {
        this.manager = manager;
        UnsafeAccess.UNSAFE.putOrderedObject(PipelineUtils.class, SERVER_CHILD_OFFSET, this);
    }

    @Override
    protected void initChannel(Channel channel) {
        manager.incrementConnectionCount();
        if (oldChildHandler != null) Utils.invoke(oldChildHandler, initChannel, channel);
    }

}
