package net.lenni0451.miniconnect.server.protocol.packets.play.s2c;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.types.version.VersionedTypes;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.PacketTypes;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class S2CSetEquipmentPacket implements Packet {

    public int entityId;
    public List<Pair<Integer, Item>> equipment;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) {
        PacketTypes.writeVarInt(byteBuf, this.entityId);
        int count = this.equipment.size();
        for (int i = 0; i < count; i++) {
            boolean last = i == count - 1;
            Pair<Integer, Item> pair = this.equipment.get(i);
            int slotMask = pair.getLeft();
            byteBuf.writeByte(!last ? slotMask | -128 : slotMask);
            VersionedTypes.V1_21_4.item.write(byteBuf, pair.getRight());
        }
    }

}
