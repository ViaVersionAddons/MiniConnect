package net.lenni0451.miniconnect.protocol.packets.play.s2c;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.miniconnect.protocol.ProtocolConstants;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.PacketTypes;

@NoArgsConstructor
@AllArgsConstructor
public class S2COpenScreenPacket implements Packet {

    public int id;
    public int type;
    public ATextComponent title;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) {
        PacketTypes.writeVarInt(byteBuf, this.id);
        PacketTypes.writeVarInt(byteBuf, this.type);
        PacketTypes.writeUnnamedTag(byteBuf, ProtocolConstants.TEXT_CODEC.serializeNbt(this.title));
    }

}