package net.lenni0451.miniconnect.server.protocol.packets.config;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.lenni0451.mcstructs.nbt.NbtTag;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.PacketTypes;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class S2CConfigRegistryDataPacket implements Packet {

    public String registry;
    public CompoundTag data;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) {
        PacketTypes.writeString(byteBuf, this.registry);
        PacketTypes.writeVarInt(byteBuf, this.data.size());
        for (Map.Entry<String, NbtTag> entry : this.data) {
            PacketTypes.writeString(byteBuf, entry.getKey());
            byteBuf.writeBoolean(true);
            PacketTypes.writeUnnamedTag(byteBuf, entry.getValue());
        }
    }

}
