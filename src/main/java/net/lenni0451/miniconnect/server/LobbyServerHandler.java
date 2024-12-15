package net.lenni0451.miniconnect.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;
import net.lenni0451.miniconnect.server.states.*;
import net.raphimc.netminecraft.constants.ConnectionState;
import net.raphimc.netminecraft.packet.Packet;

import java.util.concurrent.TimeUnit;

public class LobbyServerHandler extends SimpleChannelInboundHandler<Packet> {

    private StateHandler handler;
    private ScheduledFuture<?> tickTask;

    public void update(final Channel channel, final ConnectionState state) {
        this.handler = switch (state) {
            case HANDSHAKING -> new HandshakeStateHandler(this, channel);
            case CONFIGURATION -> new ConfigurationStateHandler(this, channel);
            case LOGIN -> new LoginStateHandler(this, channel);
            case PLAY -> new PlayStateHandler(this, channel);
            case STATUS -> new StatusStateHandler(this, channel);
        };
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        this.handler = new HandshakeStateHandler(this, ctx.channel());
        this.tickTask = ctx.channel().eventLoop().scheduleAtFixedRate(() -> this.handler.tick(), 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        this.tickTask.cancel(false);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
//        PacketRegistry registry = ctx.channel().attr(MCPipeline.PACKET_REGISTRY_ATTRIBUTE_KEY).get();
//        int packetId = registry.getPacketId(packet);
//        MCPackets packetType = MCPackets.getPacket(registry.getConnectionState(), PacketDirection.SERVERBOUND, Main.PROTOCOL_VERSION.getVersion(), packetId);
//        System.out.println(packetType);
        this.handler.handle(packet);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
