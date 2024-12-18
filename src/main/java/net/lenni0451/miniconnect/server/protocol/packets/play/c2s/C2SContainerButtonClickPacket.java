package net.lenni0451.miniconnect.server.protocol.packets.play.c2s;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.PacketTypes;

@NoArgsConstructor
@AllArgsConstructor
public class C2SContainerButtonClickPacket implements Packet {

    public int syncId;
    public int buttonId;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) {
        this.syncId = PacketTypes.readVarInt(byteBuf);
        this.buttonId = PacketTypes.readVarInt(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) {
        throw new UnsupportedOperationException();
    }

}
