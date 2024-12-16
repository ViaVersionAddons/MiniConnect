package net.lenni0451.miniconnect.protocol.packets.play;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.miniconnect.protocol.ProtocolConstants;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.PacketTypes;

@NoArgsConstructor
@AllArgsConstructor
public class S2CSystemChatPacket implements Packet {

    public ATextComponent message;
    public boolean actionbar;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) {
        PacketTypes.writeUnnamedTag(byteBuf, ProtocolConstants.TEXT_CODEC.serializeNbt(this.message));
        byteBuf.writeBoolean(this.actionbar);
    }

}