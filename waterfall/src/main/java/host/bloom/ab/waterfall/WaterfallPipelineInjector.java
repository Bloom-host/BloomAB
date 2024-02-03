package host.bloom.ab.waterfall;

import host.bloom.ab.common.managers.CounterManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import net.md_5.bungee.netty.PipelineUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WaterfallPipelineInjector extends ChannelInitializer<Channel> {

    private final CounterManager manager;
    private final ChannelInitializer<Channel> oldChildHandler = PipelineUtils.SERVER_CHILD;
    private static final long SERVER_CHILD_OFFSET;
    private static final Method initChannel;

    static {
        initChannel = getMethod(ChannelInitializer.class, "initChannel", Channel.class);

        try {
            Field childField = PipelineUtils.class.getDeclaredField("SERVER_CHILD");
            SERVER_CHILD_OFFSET = UnsafeAccess.UNSAFE.staticFieldOffset(childField);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public WaterfallPipelineInjector(CounterManager manager) {
        this.manager = manager;
        UnsafeAccess.UNSAFE.putOrderedObject(PipelineUtils.class, SERVER_CHILD_OFFSET, this);
    }

    @Override
    protected void initChannel(Channel channel) {
        manager.incrementConnectionCount();
        if (oldChildHandler != null) invoke(oldChildHandler, initChannel, channel);
    }

    public static Method getMethod(
            Class<?> clazz,
            String method,
            boolean declared,
            Class<?>... arguments) {
        try {
            if (declared) {
                return clazz.getMethod(method, arguments);
            }
            return clazz.getDeclaredMethod(method, arguments);
        } catch (NoSuchMethodException exception) {
            return null;
        }
    }

    /**
     * Get a method from a class, it doesn't matter if the method is public or not. This method will
     * first try to get a declared method and if that fails it'll try to get a public method.
     *
     * @param clazz      the class to get the method from
     * @param methodName the name of the method to find
     * @param arguments  the classes of the method arguments
     * @return the requested method if it has been found, otherwise null
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... arguments) {
        Method method = getMethod(clazz, methodName, true, arguments);
        if (method != null) {
            return method;
        }
        return getMethod(clazz, methodName, false, arguments);
    }

    public static Object invoke(Object instance, Method method, Object... arguments) {
        if (method == null) {
            return null;
        }
        makeAccessible(method);
        try {
            return method.invoke(instance, arguments);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static <T extends AccessibleObject> T makeAccessible(T accessibleObject) {
        if (!accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;
    }

}
