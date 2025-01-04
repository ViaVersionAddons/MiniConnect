package net.lenni0451.miniconnect.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;
import net.lenni0451.miniconnect.Main;
import net.lenni0451.miniconnect.model.AttributeKeys;
import net.lenni0451.miniconnect.model.ConnectionInfo;
import net.lenni0451.miniconnect.model.HandshakeData;
import net.lenni0451.miniconnect.server.model.PlayerConfig;
import net.lenni0451.miniconnect.server.states.*;
import net.lenni0451.miniconnect.utils.ChannelUtils;
import net.raphimc.netminecraft.constants.ConnectionState;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.viaproxy.util.logging.Logger;

import java.nio.channels.ClosedChannelException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LobbyServerHandler extends SimpleChannelInboundHandler<Packet> {

    private PlayerConfig playerConfig;
    private StateHandler handler;
    private ScheduledFuture<?> tickTask;

    public void loadPlayerConfig(final Channel channel, final UUID uuid) {
        ConnectionInfo previousConnectionInfo = Main.getInstance().getStateRegistry().getLobbyTargets().remove(ChannelUtils.getChannelAddress(channel));
        HandshakeData handshakeData = channel.attr(AttributeKeys.HANDSHAKE_DATA).get();
        this.playerConfig = new PlayerConfig(uuid);
        try {
            if (Main.getInstance().getStateRegistry().getVerificationQueue().remove(uuid)) {
                this.playerConfig.save();
            } else {
                this.playerConfig.load();
            }
        } catch (Throwable t) {
            Logger.LOGGER.error("Failed to load settings file for player {}", uuid, t);
        }
        if (previousConnectionInfo != null) {
            this.playerConfig.applyConnectionInfo(previousConnectionInfo);
        }
        this.playerConfig.handshakeAddress = handshakeData.host();
        this.playerConfig.handshakePort = handshakeData.port();
        this.playerConfig.clientVersion = handshakeData.clientVersion();
    }

    public PlayerConfig getPlayerConfig() {
        return this.playerConfig;
    }

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
//        MCPackets packetType = MCPackets.getPacket(registry.getConnectionState(), PacketDirection.SERVERBOUND, ProtocolConstants.PROTOCOL_VERSION.getVersion(), packetId);
//        System.out.println(packetType);
        this.handler.handle(packet);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof ClosedChannelException) return;
        cause.printStackTrace();
        ctx.close();
    }

}
