package net.lenni0451.miniconnect;

import com.google.common.cache.CacheBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.model.ConnectionInfo;
import net.lenni0451.miniconnect.protocol.ProtocolConstants;
import net.lenni0451.miniconnect.server.LobbyServerHandler;
import net.lenni0451.miniconnect.server.LobbyServerInitializer;
import net.raphimc.netminecraft.constants.IntendedState;
import net.raphimc.netminecraft.constants.MCPipeline;
import net.raphimc.netminecraft.netty.connection.NetServer;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.impl.handshaking.C2SHandshakingClientIntentionPacket;
import net.raphimc.netminecraft.util.MinecraftServerAddress;
import net.raphimc.viaproxy.ViaProxy;
import net.raphimc.viaproxy.plugins.ViaProxyPlugin;
import net.raphimc.viaproxy.plugins.events.Client2ProxyChannelInitializeEvent;
import net.raphimc.viaproxy.plugins.events.ConnectEvent;
import net.raphimc.viaproxy.plugins.events.PreConnectEvent;
import net.raphimc.viaproxy.plugins.events.types.ITyped;
import net.raphimc.viaproxy.proxy.session.UserOptions;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main extends ViaProxyPlugin {

    private static final AttributeKey<ConnectionInfo> CONNECTION_INFO = AttributeKey.newInstance("MiniConnect_ConnectionInfo");
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
    public void onClient2ProxyChannelInitialize(Client2ProxyChannelInitializeEvent event) {
        if (!event.getType().equals(ITyped.Type.POST)) return;
        //TODO: Remove this when the ViaVersion bug is fixed
        event.getChannel().pipeline().addBefore(MCPipeline.HANDLER_HANDLER_NAME, "via-bugfix", new SimpleChannelInboundHandler<Packet>() {
            @Override
            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
                if (packet instanceof C2SHandshakingClientIntentionPacket clientIntention) {
                    if (clientIntention.intendedState.equals(IntendedState.TRANSFER)) {
                        clientIntention.intendedState = IntendedState.LOGIN;
                    }
                }
                channelHandlerContext.fireChannelRead(packet);
            }
        });
    }

    @EventHandler
    public void onPreConnect(final PreConnectEvent event) {
        ConnectionInfo target = this.connectionTargets.remove(this.getChannelAddress(event.getClientChannel()));
        if (target == null) {
            event.setServerAddress(this.lobbyServer.getChannel().localAddress());
            event.setServerVersion(ProtocolConstants.PROTOCOL_VERSION);
        } else {
            //TODO: try reconnect with older clients
            event.setServerAddress(MinecraftServerAddress.ofResolved(target.host(), target.port()));
            event.setServerVersion(target.protocolVersion());
            event.getClientChannel().attr(CONNECTION_INFO).set(target);
        }
    }

    @EventHandler
    public void onConnect(final ConnectEvent event) {
        ConnectionInfo target = event.getProxyConnection().getC2P().attr(CONNECTION_INFO).get();
        if (target != null && target.account() != null) {
            UserOptions userOptions = event.getProxyConnection().getUserOptions();
            event.getProxyConnection().setUserOptions(new UserOptions(userOptions.classicMpPass(), target.account()));
        }
    }

    private InetAddress getChannelAddress(final Channel channel) {
        return ((InetSocketAddress) channel.remoteAddress()).getAddress();
    }

}
