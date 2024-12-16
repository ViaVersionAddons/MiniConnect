package net.lenni0451.miniconnect.protocol.packets.play;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.PacketTypes;

@NoArgsConstructor
@AllArgsConstructor
public class C2SContainerClosePacket implements Packet {

    public int id;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) {
        this.id = PacketTypes.readVarInt(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) {
        throw new UnsupportedOperationException();
    }

}
