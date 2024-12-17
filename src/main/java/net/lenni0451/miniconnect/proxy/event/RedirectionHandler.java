package net.lenni0451.miniconnect.proxy.event;

import io.netty.channel.unix.DomainSocketAddress;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.Main;
import net.lenni0451.miniconnect.model.AttributeKeys;
import net.lenni0451.miniconnect.model.ConnectionInfo;
import net.lenni0451.miniconnect.proxy.StateRegistry;
import net.lenni0451.miniconnect.server.protocol.ProtocolConstants;
import net.lenni0451.miniconnect.utils.ChannelUtils;
import net.raphimc.netminecraft.util.MinecraftServerAddress;
import net.raphimc.viaproxy.ViaProxy;
import net.raphimc.viaproxy.plugins.events.PreConnectEvent;
import net.raphimc.viaproxy.plugins.events.ViaProxyLoadedEvent;

import java.net.InetAddress;

public class RedirectionHandler {

    private static final DomainSocketAddress DUMMY_SOCKET_ADDRESS = new DomainSocketAddress("/miniconnect/lobby");

    @EventHandler
    public void onViaProxyLoaded(final ViaProxyLoadedEvent events) {
        ViaProxy.getConfig().setTargetAddress(DUMMY_SOCKET_ADDRESS);
    }

    @EventHandler
    public void onPreConnect(final PreConnectEvent event) {
        StateRegistry stateRegistry = Main.getInstance().getStateRegistry();
        InetAddress channelAddress = ChannelUtils.getChannelAddress(event.getClientChannel());

        if (stateRegistry.getConnectionTargets().containsKey(channelAddress)) {
            ConnectionInfo target = stateRegistry.getConnectionTargets().remove(channelAddress);
            event.setServerAddress(MinecraftServerAddress.ofResolved(target.host(), target.port()));
            event.setServerVersion(target.protocolVersion());
            event.getClientChannel().attr(AttributeKeys.CONNECTION_INFO).set(target);
        } else if (stateRegistry.getReconnectTargets().containsKey(channelAddress) && !event.getServerAddress().equals(DUMMY_SOCKET_ADDRESS)) {
            ConnectionInfo target = Main.getInstance().getStateRegistry().getReconnectTargets().remove(ChannelUtils.getChannelAddress(event.getClientChannel()));
            event.setServerVersion(target.protocolVersion());
            event.getClientChannel().attr(AttributeKeys.CONNECTION_INFO).set(target);
        } else {
            event.setServerAddress(Main.getInstance().getLobbyServer().getChannel().localAddress());
            event.setServerVersion(ProtocolConstants.PROTOCOL_VERSION);
        }
    }

}
