package net.lenni0451.miniconnect.server.protocol.packets.play.s2c;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.PacketTypes;

@NoArgsConstructor
@AllArgsConstructor
public class S2COpenBookPacket implements Packet {

    public int hand;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) {
        PacketTypes.writeVarInt(byteBuf, this.hand);
    }

}
