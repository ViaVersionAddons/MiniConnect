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
import net.raphimc.viaproxy.plugins.events.ConnectEvent;
import net.raphimc.viaproxy.plugins.events.PreConnectEvent;
import net.raphimc.viaproxy.plugins.events.ViaProxyLoadedEvent;
import net.raphimc.viaproxy.proxy.session.UserOptions;

import java.net.InetAddress;

public class RedirectionHandler {

    private static final DomainSocketAddress DUMMY_SOCKET_ADDRESS = new DomainSocketAddress("/miniconnect/lobby");

    @EventHandler
    public void onViaProxyLoaded(final ViaProxyLoadedEvent event) {
        ViaProxy.getConfig().setTargetAddress(DUMMY_SOCKET_ADDRESS);
    }

    @EventHandler
    public void onPreConnect(final PreConnectEvent event) {
        StateRegistry stateRegistry = Main.getInstance().getStateRegistry();
        InetAddress channelAddress = ChannelUtils.getChannelAddress(event.getClientChannel());

        if (stateRegistry.getConnectionTargets().containsKey(channelAddress)) {
            //First transfer from the lobby to the target server
            //Set the target server address and version for the player to connect
            ConnectionInfo target = stateRegistry.getConnectionTargets().remove(channelAddress);
            event.setServerAddress(MinecraftServerAddress.ofResolved(target.host(), target.port()));
            event.setServerVersion(target.protocolVersion());
            event.getClientChannel().attr(AttributeKeys.CONNECTION_INFO).set(target);
        } else if (stateRegistry.getReconnectTargets().containsKey(channelAddress) && !event.getServerAddress().equals(DUMMY_SOCKET_ADDRESS)) {
            //Subsequent transfers after the first initial transfer
            //Only set the target server version since ViaProxy already knows the correct target address
            ConnectionInfo target = Main.getInstance().getStateRegistry().getReconnectTargets().remove(ChannelUtils.getChannelAddress(event.getClientChannel()));
            event.setServerVersion(target.protocolVersion());
            event.getClientChannel().attr(AttributeKeys.CONNECTION_INFO).set(target);
        } else {
            //Initial connection from the client to the lobby
            //Set the address and protocol version to the lobby server
            event.setServerAddress(Main.getInstance().getLobbyServer().getChannel().localAddress());
            event.setServerVersion(ProtocolConstants.PROTOCOL_VERSION);
            event.getClientChannel().attr(AttributeKeys.ENABLE_HAPROXY).set(true); //Enable HAProxy so the lobby server knows the player ip
        }
    }

    @EventHandler
    public void onConnect(final ConnectEvent event) {
        ConnectionInfo connectionInfo = event.getProxyConnection().getC2P().attr(AttributeKeys.CONNECTION_INFO).get();
        if (connectionInfo != null) {
            event.getProxyConnection().setUserOptions(new UserOptions(null, connectionInfo.account()));
        }
    }

}
