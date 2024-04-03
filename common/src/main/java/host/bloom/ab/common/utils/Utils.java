package host.bloom.ab.common.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class Utils {

    /**
     * Parse a string as an integer
     * @return The Integer or null if invalid
     */
    public static Integer getInteger(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static Method getMethod(
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

    /**
     * Invoke a method via reflection
     */
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

    /**
     * Make a reflection field accessible
     */
    public static <T extends AccessibleObject> T makeAccessible(T accessibleObject) {
        if (!accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;
    }

    /**
     * Loops through a string-list and returns a string builder
     *
     * @param list to convert
     * @return the string-builder
     */
    public static String convertList(List<String> list) {
        StringBuilder message = new StringBuilder();

        for (String line : list) {
            message.append(line).append("\n");
        }

        return message.toString();
    }

    public static Component color(String message) {
        return MiniMessage.miniMessage().deserialize(message);
    }
}
