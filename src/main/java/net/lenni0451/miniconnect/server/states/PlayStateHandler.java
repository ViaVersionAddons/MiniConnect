package net.lenni0451.miniconnect.server.states;

import com.viaversion.nbt.tag.CompoundTag;
import com.viaversion.viaversion.api.minecraft.chunks.*;
import io.netty.channel.Channel;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.server.LobbyServerHandler;
import net.lenni0451.miniconnect.server.protocol.ProtocolConstants;
import net.lenni0451.miniconnect.server.protocol.packets.play.c2s.C2SChatCommandPacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.c2s.C2SChatPacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.*;
import net.lenni0451.miniconnect.server.states.play.screen.ScreenHandler;
import net.lenni0451.miniconnect.server.states.play.screen.impl.MainScreen;
import net.raphimc.viabedrock.protocol.data.enums.java.GameEventType;

import java.util.ArrayList;
import java.util.function.Function;

public class PlayStateHandler extends StateHandler {

    private ScreenHandler screenHandler;

    public PlayStateHandler(final LobbyServerHandler handler, final Channel channel) {
        super(handler, channel);

        this.init();
    }

    private void init() {
        this.send(new S2CLoginPacket(0, false, 1, 1, 1, false, false, false, 3, "minecraft:the_end", 0, 3, 0, false, false, null, 0, 0, false));
        this.send(new S2CGameEventPacket(GameEventType.LEVEL_CHUNKS_LOAD_START.ordinal(), 0));
        for (int i = 0; i < 9; i++) {
            Chunk chunk = new Chunk1_18(i % 3, i / 3, new ChunkSection[ProtocolConstants.CHUNK_SECTION_COUNT], new CompoundTag(), new ArrayList<>());
            for (int s = 0; s < chunk.getSections().length; s++) {
                ChunkSection section = new ChunkSectionImpl(false);
                chunk.getSections()[s] = section;
                section.palette(PaletteType.BLOCKS).addId(0);
                section.addPalette(PaletteType.BIOMES, new DataPaletteImpl(ChunkSection.BIOME_SIZE));
                section.palette(PaletteType.BIOMES).addId(0);
            }
            this.send(new S2CLevelChunkWithLightPacket(chunk));
        }
        this.send(new S2CPlayerPositionPacket(0, 24, 1, 24, 0, 0, 0, 0, 0, 0));

        this.screenHandler = new ScreenHandler(this);
        this.handlerManager.register(this.screenHandler);
        this.screenHandler.openScreen(new MainScreen());
    }

    @Override
    public void tick() {
        this.send(new S2CKeepAlivePacket(0));
    }

    @EventHandler
    public void handle(final C2SChatPacket packet) {
        if (this.handler.getPlayerConfig().chatListener != null) {
            Function<String, Boolean> listener = this.handler.getPlayerConfig().chatListener;
            if (listener.apply(packet.message)) {
                this.handler.getPlayerConfig().chatListener = null;
            }
        }
    }

    @EventHandler
    public void handle(final C2SChatCommandPacket packet) {
        if (this.handler.getPlayerConfig().chatListener != null) {
            Function<String, Boolean> listener = this.handler.getPlayerConfig().chatListener;
            if (listener.apply("/" + packet.message)) {
                this.handler.getPlayerConfig().chatListener = null;
            }
        }
    }

}
