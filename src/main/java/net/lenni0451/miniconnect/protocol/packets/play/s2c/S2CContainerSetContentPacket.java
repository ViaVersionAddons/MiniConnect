package net.lenni0451.miniconnect.protocol.packets.play.s2c;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.types.version.Types1_21_4;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.PacketTypes;

@NoArgsConstructor
@AllArgsConstructor
public class S2CContainerSetContentPacket implements Packet {

    public int windowId;
    public int revision;
    public Item[] items;
    public Item cursor;

    @Override
    public void read(ByteBuf byteBuf, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(ByteBuf byteBuf, int i) {
        PacketTypes.writeVarInt(byteBuf, this.windowId);
        PacketTypes.writeVarInt(byteBuf, this.revision);
        Types1_21_4.ITEM_ARRAY.write(byteBuf, this.items);
        Types1_21_4.ITEM.write(byteBuf, this.cursor);
    }

}
