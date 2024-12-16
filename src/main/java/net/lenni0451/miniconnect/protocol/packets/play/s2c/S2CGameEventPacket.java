package net.lenni0451.miniconnect.protocol.packets.play.s2c;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.raphimc.netminecraft.packet.Packet;

@NoArgsConstructor
@AllArgsConstructor
public class S2CGameEventPacket implements Packet {

    public int event;
    public float value;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) {
        byteBuf.writeByte(this.event);
        byteBuf.writeFloat(this.value);
    }

}
