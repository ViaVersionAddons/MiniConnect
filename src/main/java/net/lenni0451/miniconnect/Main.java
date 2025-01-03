package net.lenni0451.miniconnect;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.proxy.StateRegistry;
import net.lenni0451.miniconnect.proxy.event.HAProxyEnableHandler;
import net.lenni0451.miniconnect.proxy.event.PacketHandlerRegistry;
import net.lenni0451.miniconnect.proxy.event.RedirectionHandler;
import net.lenni0451.miniconnect.proxy.event.ViaLoadHandler;
import net.lenni0451.miniconnect.server.LobbyServerInitializer;
import net.raphimc.netminecraft.constants.IntendedState;
import net.raphimc.netminecraft.constants.MCPipeline;
import net.raphimc.netminecraft.netty.connection.NetServer;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.impl.handshaking.C2SHandshakingClientIntentionPacket;
import net.raphimc.viaproxy.ViaProxy;
import net.raphimc.viaproxy.plugins.ViaProxyPlugin;
import net.raphimc.viaproxy.plugins.events.Client2ProxyChannelInitializeEvent;
import net.raphimc.viaproxy.plugins.events.types.ITyped;

import java.net.InetSocketAddress;

public class Main extends ViaProxyPlugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }


    private StateRegistry stateRegistry;
    private NetServer lobbyServer;

    public Main() {
        instance = this;
    }

    public NetServer getLobbyServer() {
        return this.lobbyServer;
    }

    public StateRegistry getStateRegistry() {
        return this.stateRegistry;
    }

    @Override
    public void onEnable() {
        this.stateRegistry = new StateRegistry();
        this.lobbyServer = new NetServer(new LobbyServerInitializer());
        this.lobbyServer.bind(new InetSocketAddress("localhost", 0), false);

        ViaProxy.EVENT_MANAGER.register(this); //TODO: Remove this when the ViaVersion bug is fixed
        ViaProxy.EVENT_MANAGER.register(new RedirectionHandler());
        ViaProxy.EVENT_MANAGER.register(new PacketHandlerRegistry());
        ViaProxy.EVENT_MANAGER.register(new ViaLoadHandler());
        ViaProxy.EVENT_MANAGER.register(new HAProxyEnableHandler());
    }

    @EventHandler
    public void onClient2ProxyChannelInitialize(final Client2ProxyChannelInitializeEvent event) {
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

}
