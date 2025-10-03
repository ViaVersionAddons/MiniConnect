package net.lenni0451.miniconnect.server.protocol.packets.play.s2c;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.lenni0451.miniconnect.server.protocol.packets.model.CommonPlayerSpawnInfo;
import net.raphimc.netminecraft.packet.Packet;

@NoArgsConstructor
@AllArgsConstructor
public class S2CRespawnPacket implements Packet {

    public static final byte KEEP_ATTRIBUTE_MODIFIERS = 0b01;
    public static final byte KEEP_ENTITY_DATA = 0b10;
    public static final byte KEEP_ALL_DATA = 0b11;

    public CommonPlayerSpawnInfo spawnInfo;
    public byte dataToKeep;

    @Override
    public void read(ByteBuf byteBuf, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(ByteBuf byteBuf, int i) {
        this.spawnInfo.write(byteBuf);
        byteBuf.writeByte(this.dataToKeep);
    }

}
