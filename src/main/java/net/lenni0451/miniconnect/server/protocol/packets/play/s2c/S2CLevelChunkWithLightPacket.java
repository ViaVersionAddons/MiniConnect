package net.lenni0451.miniconnect.server.protocol.packets.play.s2c;

import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_20_2;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.lenni0451.miniconnect.server.protocol.ProtocolConstants;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.PacketTypes;

import java.util.BitSet;

@NoArgsConstructor
@AllArgsConstructor
public class S2CLevelChunkWithLightPacket implements Packet {

    private static final Type<Chunk> CHUNK_TYPE = new ChunkType1_20_2(ProtocolConstants.CHUNK_SECTION_COUNT, 15, 7);

    public Chunk chunk;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) {
        CHUNK_TYPE.write(byteBuf, this.chunk);
        BitSet emptyLightMask = new BitSet();
        int lightCount = this.chunk.getSections().length + 2;
        emptyLightMask.set(0, lightCount);
        Types.LONG_ARRAY_PRIMITIVE.write(byteBuf, emptyLightMask.toLongArray());
        Types.LONG_ARRAY_PRIMITIVE.write(byteBuf, new long[0]);
        Types.LONG_ARRAY_PRIMITIVE.write(byteBuf, new long[0]);
        Types.LONG_ARRAY_PRIMITIVE.write(byteBuf, emptyLightMask.toLongArray());
        PacketTypes.writeVarInt(byteBuf, lightCount);
        for (int i = 0; i < lightCount; i++) Types.BYTE_ARRAY_PRIMITIVE.write(byteBuf, ProtocolConstants.FULL_LIGHT);
        PacketTypes.writeVarInt(byteBuf, 0);
    }

}
