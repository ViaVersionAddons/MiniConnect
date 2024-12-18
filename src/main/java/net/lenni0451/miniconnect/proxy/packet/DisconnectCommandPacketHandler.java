package net.lenni0451.miniconnect.proxy.packet;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import net.lenni0451.miniconnect.Main;
import net.lenni0451.miniconnect.model.AttributeKeys;
import net.lenni0451.miniconnect.model.ConnectionInfo;
import net.lenni0451.miniconnect.utils.ChannelUtils;
import net.raphimc.netminecraft.constants.ConnectionState;
import net.raphimc.netminecraft.constants.MCPackets;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.PacketTypes;
import net.raphimc.netminecraft.packet.UnknownPacket;
import net.raphimc.netminecraft.packet.impl.play.S2CPlayTransferPacket;
import net.raphimc.viaproxy.proxy.packethandler.PacketHandler;
import net.raphimc.viaproxy.proxy.session.ProxyConnection;

import java.util.List;
import java.util.Locale;

public class DisconnectCommandPacketHandler extends PacketHandler {

    private final int packetId;

    public DisconnectCommandPacketHandler(final ProxyConnection proxyConnection) {
        super(proxyConnection);

        if (proxyConnection.getClientVersion().newerThanOrEqualTo(ProtocolVersion.v1_19)) {
            this.packetId = MCPackets.C2S_CHAT_COMMAND.getId(proxyConnection.getClientVersion().getVersion());
        } else {
            this.packetId = MCPackets.C2S_CHAT.getId(proxyConnection.getClientVersion().getVersion());
        }
    }

    @Override
    public boolean handleC2P(Packet packet, List<ChannelFutureListener> listeners) {
        if (!this.proxyConnection.getC2pConnectionState().equals(ConnectionState.PLAY)) return true;
        if (!(packet instanceof UnknownPacket unknownPacket)) return true;
        if (unknownPacket.packetId != this.packetId) return true;

        ByteBuf buf = Unpooled.wrappedBuffer(unknownPacket.data);
        String message = PacketTypes.readString(buf, 256);
        if (this.proxyConnection.getClientVersion().newerThanOrEqualTo(ProtocolVersion.v1_19)) message = "/" + message;
        if (message.toLowerCase(Locale.ROOT).equals("/disconnect")) {
            this.reconnect();
            return false;
        }
        return true;
    }

    private void reconnect() {
        ConnectionInfo connectionInfo = this.proxyConnection.getC2P().attr(AttributeKeys.CONNECTION_INFO).get();
        if (connectionInfo == null) {
            //If the connectionInfo is null, the player is currently on the lobby server
            return;
        }
        Main.getInstance().getStateRegistry().getLobbyTargets().put(ChannelUtils.getChannelAddress(this.proxyConnection.getC2P()), connectionInfo);
        this.proxyConnection.getC2P().writeAndFlush(new S2CPlayTransferPacket(connectionInfo.handshakeAddress(), connectionInfo.handshakePort()));
    }

}
