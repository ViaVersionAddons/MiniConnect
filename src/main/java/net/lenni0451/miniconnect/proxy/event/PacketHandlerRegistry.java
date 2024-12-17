package net.lenni0451.miniconnect.proxy.event;

import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.proxy.packet.DisconnectCommandPacketHandler;
import net.lenni0451.miniconnect.proxy.packet.ReconnectPacketHandler;
import net.raphimc.viaproxy.plugins.events.ConnectEvent;
import net.raphimc.viaproxy.proxy.packethandler.PacketHandler;

import java.util.List;

public class PacketHandlerRegistry {

    @EventHandler
    public void onConnect(final ConnectEvent event) {
        List<PacketHandler> packetHandlers = event.getProxyConnection().getPacketHandlers();
        packetHandlers.add(new DisconnectCommandPacketHandler(event.getProxyConnection()));
        packetHandlers.add(0, new ReconnectPacketHandler(event.getProxyConnection()));
    }

}
