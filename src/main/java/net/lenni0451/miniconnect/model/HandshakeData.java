package net.lenni0451.miniconnect.model;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.buffer.ByteBuf;
import net.raphimc.netminecraft.packet.PacketTypes;

public record HandshakeData(String host, int port, ProtocolVersion clientVersion) {

    public static HandshakeData read(final ByteBuf buf) {
        String host = PacketTypes.readString(buf, Short.MAX_VALUE);
        int port = buf.readUnsignedShort();
        ProtocolVersion clientVersion = ProtocolVersion.getProtocol(buf.readInt());
        return new HandshakeData(host, port, clientVersion);
    }

    public void write(final ByteBuf buf) {
        PacketTypes.writeString(buf, this.host);
        buf.writeShort(this.port);
        buf.writeInt(this.clientVersion.getOriginalVersion());
    }

}
