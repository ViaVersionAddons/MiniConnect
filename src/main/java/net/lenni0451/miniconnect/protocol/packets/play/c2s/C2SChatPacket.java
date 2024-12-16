package net.lenni0451.miniconnect.protocol.packets.play.c2s;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.PacketTypes;

import java.time.Instant;
import java.util.BitSet;

@NoArgsConstructor
@AllArgsConstructor
public class C2SChatPacket implements Packet {

    public String message;
    public Instant timestamp;
    public long salt;
    public byte[] signature;
    public int offset;
    public BitSet acknowledged;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) {
        this.message = PacketTypes.readString(byteBuf, 256);
        this.timestamp = Instant.ofEpochMilli(byteBuf.readLong());
        this.salt = byteBuf.readLong();
        if (byteBuf.readBoolean()) {
            this.signature = PacketTypes.readByteArray(byteBuf);
        }
        this.offset = PacketTypes.readVarInt(byteBuf);
        byte[] acknowledgedBytes = new byte[3];
        byteBuf.readBytes(acknowledgedBytes);
        this.acknowledged = BitSet.valueOf(acknowledgedBytes);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) {
        throw new UnsupportedOperationException();
    }

}
