package net.lenni0451.miniconnect.server.protocol.packets.play.c2s;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.types.version.VersionedTypes;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.PacketTypes;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class C2SContainerClickPacket implements Packet {

    public int containerId;
    public int revision;
    public int slot;
    public int button;
    public int action;
    public Map<Integer, Item> modifiedStacks;
    public Item item;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) {
        this.containerId = PacketTypes.readVarInt(byteBuf);
        this.revision = PacketTypes.readVarInt(byteBuf);
        this.slot = byteBuf.readShort();
        this.button = byteBuf.readByte();
        this.action = PacketTypes.readVarInt(byteBuf);
        int entries = PacketTypes.readVarInt(byteBuf);
        if (entries > 128) throw new DecoderException("Too many entries in C2SContainerClickPacket (" + entries + " > 128)");
        this.modifiedStacks = new HashMap<>();
        for (int i = 0; i < entries; i++) {
            int key = byteBuf.readShort();
            Item value = VersionedTypes.V1_21_4.item.read(byteBuf);
            this.modifiedStacks.put(key, value);
        }
        this.item = VersionedTypes.V1_21_4.item.read(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) {
        throw new UnsupportedOperationException();
    }

}
