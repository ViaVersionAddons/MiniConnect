package net.lenni0451.miniconnect.server.protocol.packets.play.s2c;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.lenni0451.miniconnect.server.protocol.ProtocolConstants;
import net.lenni0451.miniconnect.server.protocol.packets.model.CommonPlayerSpawnInfo;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.PacketTypes;

@NoArgsConstructor
@AllArgsConstructor
public class S2CLoginPacket implements Packet {

    public int playerId;
    public boolean hardcode;
    public int maxPlayers;
    public int chunkRenderDistance;
    public int simulationDistance;
    public boolean reduceDebugInfo;
    public boolean showDeathScreen;
    public boolean doLimitedCrafting;
    public CommonPlayerSpawnInfo spawnInfo;
    public boolean enforceSecureChat;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) {
        byteBuf.writeInt(this.playerId);
        byteBuf.writeBoolean(this.hardcode);
        PacketTypes.writeVarInt(byteBuf, ProtocolConstants.DIMENSIONS.length);
        for (String dimension : ProtocolConstants.DIMENSIONS) {
            PacketTypes.writeString(byteBuf, dimension);
        }
        PacketTypes.writeVarInt(byteBuf, this.maxPlayers);
        PacketTypes.writeVarInt(byteBuf, this.chunkRenderDistance);
        PacketTypes.writeVarInt(byteBuf, this.simulationDistance);
        byteBuf.writeBoolean(this.reduceDebugInfo);
        byteBuf.writeBoolean(this.showDeathScreen);
        byteBuf.writeBoolean(this.doLimitedCrafting);
        this.spawnInfo.write(byteBuf);
        byteBuf.writeBoolean(this.enforceSecureChat);
    }

}
