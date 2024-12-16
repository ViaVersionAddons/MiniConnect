package net.lenni0451.miniconnect.protocol;

import net.lenni0451.miniconnect.protocol.packets.config.S2CConfigRegistryDataPacket;
import net.lenni0451.miniconnect.protocol.packets.config.S2CConfigUpdateTagsPacket;
import net.lenni0451.miniconnect.protocol.packets.play.c2s.C2SChatPacket;
import net.lenni0451.miniconnect.protocol.packets.play.c2s.C2SContainerClickPacket;
import net.lenni0451.miniconnect.protocol.packets.play.c2s.C2SContainerClosePacket;
import net.lenni0451.miniconnect.protocol.packets.play.s2c.*;
import net.raphimc.netminecraft.constants.MCPackets;
import net.raphimc.netminecraft.packet.registry.DefaultPacketRegistry;

public class LobbyPacketRegistry extends DefaultPacketRegistry {

    public LobbyPacketRegistry() {
        super(false, ProtocolConstants.PROTOCOL_VERSION.getVersion());

        //config
        this.registerPacket(MCPackets.S2C_CONFIG_REGISTRY_DATA, S2CConfigRegistryDataPacket::new);
        this.registerPacket(MCPackets.S2C_CONFIG_UPDATE_TAGS, S2CConfigUpdateTagsPacket::new);
        //play
        this.registerPacket(MCPackets.C2S_CHAT, C2SChatPacket::new);
        this.registerPacket(MCPackets.C2S_CONTAINER_CLOSE, C2SContainerClosePacket::new);
        this.registerPacket(MCPackets.C2S_CONTAINER_CLICK, C2SContainerClickPacket::new);

        this.registerPacket(MCPackets.S2C_LOGIN, S2CLoginPacket::new);
        this.registerPacket(MCPackets.S2C_KEEP_ALIVE, S2CKeepAlivePacket::new);
        this.registerPacket(MCPackets.S2C_GAME_EVENT, S2CGameEventPacket::new);
        this.registerPacket(MCPackets.S2C_LEVEL_CHUNK_WITH_LIGHT, S2CLevelChunkWithLightPacket::new);
        this.registerPacket(MCPackets.S2C_PLAYER_POSITION, S2CPlayerPositionPacket::new);
        this.registerPacket(MCPackets.S2C_SYSTEM_CHAT, S2CSystemChatPacket::new);
        this.registerPacket(MCPackets.S2C_CONTAINER_CLOSE, S2CContainerClosePacket::new);
        this.registerPacket(MCPackets.S2C_OPEN_SCREEN, S2COpenScreenPacket::new);
        this.registerPacket(MCPackets.S2C_TRANSFER, S2CTransferPacket::new);
        this.registerPacket(MCPackets.S2C_CONTAINER_SET_CONTENT, S2CContainerSetContentPacket::new);
    }

}
