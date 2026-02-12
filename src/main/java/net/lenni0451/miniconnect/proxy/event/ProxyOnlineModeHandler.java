package net.lenni0451.miniconnect.proxy.event;

import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.Main;
import net.lenni0451.miniconnect.server.model.PlayerConfig;
import net.lenni0451.miniconnect.utils.UUIDUtils;
import net.raphimc.viaproxy.ViaProxy;
import net.raphimc.viaproxy.plugins.events.ShouldVerifyOnlineModeEvent;
import net.raphimc.viaproxy.plugins.events.ViaProxyLoadedEvent;
import net.raphimc.viaproxy.util.logging.Logger;

import java.io.File;
import java.util.UUID;

public class ProxyOnlineModeHandler {

    public ProxyOnlineModeHandler() {
        try {
            File dataDir = Main.getInstance().getDataFolder();
            if (!dataDir.exists()) return;
            for (File file : dataDir.listFiles()) {
                if (!file.isFile()) continue;
                if (!file.getName().endsWith(".dat")) continue;
                File target = new File(PlayerConfig.baseDir(), file.getName());
                if (target.exists()) {
                    Logger.LOGGER.warn("Skipping migration of {} as it already exists in the target directory", file.getName());
                    continue;
                }
                target.getParentFile().mkdirs();
                file.renameTo(target);
                Logger.LOGGER.info("Migrated online mode settings file {} to new directory", file.getName());
            }
        } catch (Throwable ignored) {
        }
    }

    @EventHandler
    public void onViaProxyLoaded(final ViaProxyLoadedEvent event) {
        ViaProxy.getConfig().setProxyOnlineMode(true);
    }

    @EventHandler
    public void onShouldVerifyOnlineMode(final ShouldVerifyOnlineModeEvent event) {
        UUID uuid = event.getProxyConnection().getGameProfile().getId();
        if (Main.getInstance().getStateRegistry().getVerificationQueue().contains(uuid)) {
            event.setCancelled(false); //Enforce online mode verification
        } else {
            String hashedUUID = UUIDUtils.hash(uuid);
            File settingsFile = new File(PlayerConfig.baseDir(), hashedUUID + ".dat");
            if (!settingsFile.exists()) event.setCancelled(true);
        }
    }

}
