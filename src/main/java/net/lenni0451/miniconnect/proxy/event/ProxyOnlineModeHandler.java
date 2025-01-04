package net.lenni0451.miniconnect.proxy.event;

import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.Main;
import net.lenni0451.miniconnect.utils.UUIDUtils;
import net.raphimc.viaproxy.ViaProxy;
import net.raphimc.viaproxy.plugins.events.ShouldVerifyOnlineModeEvent;
import net.raphimc.viaproxy.plugins.events.ViaProxyLoadedEvent;

import java.io.File;
import java.util.UUID;

public class ProxyOnlineModeHandler {

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
            File settingsFile = new File(Main.getInstance().getDataFolder(), hashedUUID + ".dat");
            if (!settingsFile.exists()) event.setCancelled(true);
        }
    }

}
