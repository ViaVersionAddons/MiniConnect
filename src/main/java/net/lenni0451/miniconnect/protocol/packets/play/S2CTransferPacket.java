package net.lenni0451.miniconnect.protocol.packets.play;

import io.netty.buffer.ByteBuf;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.PacketTypes;

public class S2CTransferPacket implements Packet {

    public String host;
    public int port;

    @Override
    public void read(ByteBuf byteBuf, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(ByteBuf byteBuf, int i) {
        PacketTypes.writeString(byteBuf, this.host);
        PacketTypes.writeVarInt(byteBuf, this.port);
    }

}
