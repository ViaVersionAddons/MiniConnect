package net.lenni0451.miniconnect;

import com.google.common.cache.CacheBuilder;
import io.netty.channel.Channel;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.model.ConnectionInfo;
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

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }


    private final Map<InetAddress, ConnectionInfo> connectionTargets = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).<InetAddress, ConnectionInfo>build().asMap();
    private NetServer lobbyServer;

    public Main() {
        instance = this;
    }

    public void registerReconnect(final Channel channel, final ConnectionInfo connectionInfo) {
        this.connectionTargets.put(this.getChannelAddress(channel), connectionInfo);
    }

    @Override
    public void onEnable() {
        ViaProxy.EVENT_MANAGER.register(this);
        this.lobbyServer = new NetServer(LobbyServerHandler::new, LobbyServerInitializer::new);
        this.lobbyServer.bind(new InetSocketAddress("localhost", 0), false);
    }

    @EventHandler
    public void onPreConnect(PreConnectEvent event) {
        ConnectionInfo target = this.connectionTargets.remove(this.getChannelAddress(event.getClientChannel()));
        if (target == null) {
            event.setServerAddress(this.lobbyServer.getChannel().localAddress());
            event.setServerVersion(ProtocolConstants.PROTOCOL_VERSION);
        } else {
            //TODO: try reconnect with older clients
            event.setServerAddress(new InetSocketAddress(target.host(), target.port()));
            event.setServerVersion(target.protocolVersion());
        }
    }

    private InetAddress getChannelAddress(final Channel channel) {
        return ((InetSocketAddress) channel.remoteAddress()).getAddress();
    }

}
