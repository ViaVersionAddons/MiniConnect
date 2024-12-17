package net.lenni0451.miniconnect.server.protocol.packets.config;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.PacketTypes;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class S2CConfigUpdateTagsPacket implements Packet {

    public Map<String, Map<String, int[]>> tags;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) {
        PacketTypes.writeVarInt(byteBuf, this.tags.size());
        for (Map.Entry<String, Map<String, int[]>> entry : this.tags.entrySet()) {
            PacketTypes.writeString(byteBuf, entry.getKey());
            PacketTypes.writeVarInt(byteBuf, entry.getValue().size());
            for (Map.Entry<String, int[]> entry2 : entry.getValue().entrySet()) {
                PacketTypes.writeString(byteBuf, entry2.getKey());
                PacketTypes.writeVarInt(byteBuf, entry2.getValue().length);
                for (int i : entry2.getValue()) {
                    PacketTypes.writeVarInt(byteBuf, i);
                }
            }
        }
    }

}
