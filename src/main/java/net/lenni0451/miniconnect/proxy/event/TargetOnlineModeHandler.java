package net.lenni0451.miniconnect.proxy.event;

import net.lenni0451.lambdaevents.EventHandler;
import net.raphimc.viaproxy.plugins.events.JoinServerRequestEvent;

public class TargetOnlineModeHandler {

    @EventHandler(priority = Integer.MIN_VALUE)
    public void onJoinServerRequest(final JoinServerRequestEvent event) {
        if (event.isCancelled()) return;
        event.getProxyConnection().kickClient("§cThe server is in online mode and requires authentication.\n§aPlease reconnect and authenticate by clicking on Login (key).");
    }

}
