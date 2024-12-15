package net.lenni0451.miniconnect;

import com.google.common.cache.CacheBuilder;
import io.netty.channel.Channel;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.protocol.ProtocolConstants;
import net.lenni0451.miniconnect.server.LobbyServerHandler;
import net.lenni0451.miniconnect.server.LobbyServerInitializer;
import net.raphimc.netminecraft.netty.connection.NetServer;
import net.raphimc.viaproxy.ViaProxy;
import net.raphimc.viaproxy.plugins.ViaProxyPlugin;
import net.raphimc.viaproxy.plugins.events.PreConnectEvent;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main extends ViaProxyPlugin {

    private final Map<InetAddress, InetSocketAddress> connectionTargets = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).<InetAddress, InetSocketAddress>build().asMap();
    private NetServer lobbyServer;

    @Override
    public void onEnable() {
        ViaProxy.EVENT_MANAGER.register(this);
        this.lobbyServer = new NetServer(LobbyServerHandler::new, LobbyServerInitializer::new);
        this.lobbyServer.bind(new InetSocketAddress("localhost", 0), false);
    }

    @EventHandler
    public void onPreConnect(PreConnectEvent event) {
        InetSocketAddress target = this.connectionTargets.remove(this.getChannelAddress(event.getClientChannel()));
        if (target == null) {
            event.setServerAddress(this.lobbyServer.getChannel().localAddress());
            event.setServerVersion(ProtocolConstants.PROTOCOL_VERSION);
        } else {
            //TODO: try reconnect with older clients
            event.setServerAddress(target);
        }
    }

    private InetAddress getChannelAddress(final Channel channel) {
        return ((InetSocketAddress) channel.remoteAddress()).getAddress();
    }

}
