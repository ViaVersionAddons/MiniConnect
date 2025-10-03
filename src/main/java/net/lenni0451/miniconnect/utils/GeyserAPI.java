package net.lenni0451.miniconnect.utils;

import net.lenni0451.reflect.accessor.MethodAccessor;
import net.raphimc.viaproxy.ViaProxy;
import net.raphimc.viaproxy.plugins.ViaProxyPlugin;
import net.raphimc.viaproxy.util.logging.Logger;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Use reflection to access the Geyser API because directly invoking it doesn't work because of the classloader isolation.
 */
public class GeyserAPI {

    private static final Predicate<UUID> isBedrockPlayer;

    static {
        Predicate<UUID> tempIsBedrockPlayer = uuid -> false;
        ViaProxyPlugin plugin = ViaProxy.getPluginManager().getPlugin("Geyser-ViaProxy");
        if (plugin != null) {
            try {
                ClassLoader geyserClassLoader = plugin.getClassLoader();
                Class<?> geyserClass = Class.forName("org.geysermc.api.Geyser", false, geyserClassLoader);
                Method apiMethod = geyserClass.getDeclaredMethod("api");
                Object geyserApiInstance = apiMethod.invoke(null);
                Method isBedrockPlayerMethod = geyserApiInstance.getClass().getDeclaredMethod("isBedrockPlayer", UUID.class);
                tempIsBedrockPlayer = MethodAccessor.makeInvoker(Predicate.class, geyserApiInstance, isBedrockPlayerMethod);
            } catch (Throwable t) {
                Logger.LOGGER.error("Failed to initialize Geyser-ViaProxy support", t);
            }
        }
        isBedrockPlayer = tempIsBedrockPlayer;
    }

    public static boolean isGeyserPlayer(final UUID uuid) {
        return isBedrockPlayer.test(uuid);
    }

}
