package net.lenni0451.miniconnect.protocol;

import net.lenni0451.miniconnect.protocol.packets.config.S2CConfigRegistryDataPacket;
import net.lenni0451.miniconnect.protocol.packets.config.S2CConfigUpdateTagsPacket;
import net.lenni0451.miniconnect.protocol.packets.play.*;
import net.raphimc.netminecraft.constants.MCPackets;
import net.raphimc.netminecraft.packet.registry.DefaultPacketRegistry;

public class LobbyPacketRegistry extends DefaultPacketRegistry {

    public LobbyPacketRegistry() {
        super(false, ProtocolConstants.PROTOCOL_VERSION.getVersion());

        //config
        this.registerPacket(MCPackets.S2C_CONFIG_REGISTRY_DATA, S2CConfigRegistryDataPacket::new);
        this.registerPacket(MCPackets.S2C_CONFIG_UPDATE_TAGS, S2CConfigUpdateTagsPacket::new);
        //play
        this.registerPacket(MCPackets.S2C_LOGIN, S2CLoginPacket::new);
        this.registerPacket(MCPackets.S2C_KEEP_ALIVE, S2CKeepAlivePacket::new);
        this.registerPacket(MCPackets.S2C_GAME_EVENT, S2CGameEventPacket::new);
        this.registerPacket(MCPackets.S2C_LEVEL_CHUNK_WITH_LIGHT, S2CLevelChunkWithLightPacket::new);
        this.registerPacket(MCPackets.S2C_PLAYER_POSITION, S2CPlayerPositionPacket::new);
    }

}
