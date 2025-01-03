package net.lenni0451.miniconnect.server.protocol.packets.play.c2s;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.PacketTypes;

@NoArgsConstructor
@AllArgsConstructor
public class C2SChatCommandPacket implements Packet {

    public String message;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) {
        this.message = PacketTypes.readString(byteBuf, Short.MAX_VALUE);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) {
        throw new UnsupportedOperationException();
    }

}
