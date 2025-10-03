package net.lenni0451.miniconnect.server.protocol.packets.model;

import com.viaversion.viaversion.api.minecraft.GlobalBlockPosition;
import com.viaversion.viaversion.api.type.Types;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.raphimc.netminecraft.packet.PacketTypes;

@NoArgsConstructor
@AllArgsConstructor
public class CommonPlayerSpawnInfo {

    public int dimension;
    public String world;
    public long levelSeed;
    public int gamemode;
    public int previousGamemode;
    public boolean debugWorld;
    public boolean flatWorld;
    public GlobalBlockPosition lastDeath;
    public int portalCooldown;
    public int seaLevel;

    public void write(final ByteBuf byteBuf) {
        PacketTypes.writeVarInt(byteBuf, this.dimension);
        PacketTypes.writeString(byteBuf, this.world);
        byteBuf.writeLong(this.levelSeed);
        byteBuf.writeByte(this.gamemode);
        byteBuf.writeByte(this.previousGamemode);
        byteBuf.writeBoolean(this.debugWorld);
        byteBuf.writeBoolean(this.flatWorld);
        Types.OPTIONAL_GLOBAL_POSITION.write(byteBuf, this.lastDeath);
        PacketTypes.writeVarInt(byteBuf, this.portalCooldown);
        PacketTypes.writeVarInt(byteBuf, this.seaLevel);
    }

    public CommonPlayerSpawnInfo copy() {
        return new CommonPlayerSpawnInfo(this.dimension, this.world, this.levelSeed, this.gamemode, this.previousGamemode, this.debugWorld, this.flatWorld, this.lastDeath, this.portalCooldown, this.seaLevel);
    }

}
