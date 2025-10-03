package net.lenni0451.miniconnect.server.protocol;

import com.viaversion.viabackwards.protocol.v1_21_4to1_21_2.Protocol1_21_4To1_21_2;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.chunks.*;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import lombok.SneakyThrows;
import net.lenni0451.commons.gson.GsonParser;
import net.lenni0451.commons.gson.elements.GsonElement;
import net.lenni0451.mcstructs.nbt.NbtTag;
import net.lenni0451.mcstructs.nbt.io.NbtIO;
import net.lenni0451.mcstructs.nbt.io.NbtReadTracker;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;
import net.lenni0451.mcstructs.text.serializer.TextComponentCodec;
import net.lenni0451.miniconnect.server.protocol.packets.model.CommonPlayerSpawnInfo;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2CGameEventPacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2CLevelChunkWithLightPacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2CPlayerAbilitiesPacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2CPlayerPositionPacket;
import net.lenni0451.miniconnect.server.states.StateHandler;
import net.raphimc.viabedrock.protocol.data.enums.java.GameEventType;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;

public class ProtocolConstants {

    public static final ProtocolVersion PROTOCOL_VERSION = ProtocolVersion.v1_21_4;
    public static final TextComponentCodec TEXT_CODEC = TextComponentCodec.V1_21_4;
    public static final Map<String, CompoundTag> REGISTRIES;
    public static final Map<String, Map<String, int[]>> TAGS;
    public static final List<String> ITEMS;
    public static final String[] DIMENSIONS;
    public static final int CHUNK_SECTION_COUNT = 16; //16 for the end
    public static final byte[] FULL_LIGHT = new byte[2048];
    public static final Protocol<?, ?, ?, ?> VIA_PROTOCOL = Via.getManager().getProtocolManager().getProtocol(Protocol1_21_4To1_21_2.class);
    public static final CommonPlayerSpawnInfo DEFAULT_SPAWN_INFO = new CommonPlayerSpawnInfo(3, "minecraft:the_end", 0, 3, 0, false, false, null, 0, 0);

    static {
        REGISTRIES = readCompound("registries.nbt", tag -> {
            Map<String, CompoundTag> registries = new HashMap<>();
            for (Map.Entry<String, NbtTag> entry : tag) {
                registries.put(entry.getKey(), entry.getValue().asCompoundTag());
            }
            return registries;
        });
        TAGS = readCompound("tags.nbt", tag -> {
            Map<String, Map<String, int[]>> tags = new HashMap<>();
            for (Map.Entry<String, NbtTag> entry : tag) {
                Map<String, int[]> registryTags = new HashMap<>();
                for (Map.Entry<String, NbtTag> tagEntry : entry.getValue().asCompoundTag()) {
                    registryTags.put(tagEntry.getKey(), tagEntry.getValue().asIntArrayTag().getValue());
                }
                tags.put(entry.getKey(), registryTags);
            }
            return tags;
        });
        ITEMS = readJson("items.json", element -> element.asArray().stream().map(GsonElement::asString).toList());
        DIMENSIONS = REGISTRIES.get("minecraft:dimension_type").asCompoundTag().getValue().keySet().toArray(String[]::new);
        Arrays.fill(FULL_LIGHT, (byte) -1);
    }

    @SneakyThrows
    private static <T> T readCompound(final String name, final Function<CompoundTag, T> mapper) {
        InputStream stream = ProtocolConstants.class.getClassLoader().getResourceAsStream(name);
        if (stream == null) throw new IllegalStateException("Missing resource: " + name);
        return mapper.apply(NbtIO.LATEST.read(stream, true, NbtReadTracker.unlimitedDepth()).asCompoundTag());
    }

    @SneakyThrows
    private static <T> T readJson(final String name, final Function<GsonElement, T> mapper) {
        InputStream stream = ProtocolConstants.class.getClassLoader().getResourceAsStream(name);
        if (stream == null) throw new IllegalStateException("Missing resource: " + name);
        return mapper.apply(GsonParser.parse(new InputStreamReader(stream)));
    }

    public static void sendSpawnInfo(final StateHandler stateHandler) {
        stateHandler.send(new S2CPlayerAbilitiesPacket(true, true, true, false, 0, 0));
        stateHandler.send(new S2CGameEventPacket(GameEventType.LEVEL_CHUNKS_LOAD_START.ordinal(), 0));
        for (int i = 0; i < 9; i++) {
            Chunk chunk = new Chunk1_18(i % 3, i / 3, new ChunkSection[ProtocolConstants.CHUNK_SECTION_COUNT], new com.viaversion.nbt.tag.CompoundTag(), new ArrayList<>());
            for (int s = 0; s < chunk.getSections().length; s++) {
                ChunkSection section = new ChunkSectionImpl(false);
                chunk.getSections()[s] = section;
                section.palette(PaletteType.BLOCKS).addId(0);
                section.addPalette(PaletteType.BIOMES, new DataPaletteImpl(ChunkSection.BIOME_SIZE));
                section.palette(PaletteType.BIOMES).addId(0);
            }
            stateHandler.send(new S2CLevelChunkWithLightPacket(chunk));
        }
        stateHandler.send(new S2CPlayerPositionPacket(0, 24, 1, 24, 0, 0, 0, 0, 0, 0));
    }

}
