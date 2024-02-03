package host.bloom.ab.velocity;

import host.bloom.ab.common.AbstractPlugin;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VelocityLoginHookChannel extends ChannelInitializer<Channel> {

    private final AbstractPlugin plugin;
    private final ChannelInitializer original;
    private static final Method initChannel;

    static {
        initChannel = getMethod(ChannelInitializer.class, "initChannel", Channel.class);
    }

    public VelocityLoginHookChannel(AbstractPlugin plugin, ChannelInitializer original) {
        this.plugin = plugin;
        this.original = original;
    }

    @Override
    protected void initChannel(io.netty.channel.Channel ch) {
        this.plugin.getManager().incrementConnectionCount();
        invoke(original, initChannel, ch);
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
