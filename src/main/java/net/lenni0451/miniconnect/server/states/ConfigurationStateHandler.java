package net.lenni0451.miniconnect.server.states;

import io.netty.channel.Channel;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;
import net.lenni0451.miniconnect.server.LobbyServerHandler;
import net.lenni0451.miniconnect.server.protocol.ProtocolConstants;
import net.lenni0451.miniconnect.server.protocol.packets.config.S2CConfigRegistryDataPacket;
import net.lenni0451.miniconnect.server.protocol.packets.config.S2CConfigUpdateTagsPacket;
import net.raphimc.netminecraft.constants.ConnectionState;
import net.raphimc.netminecraft.packet.impl.configuration.C2SConfigFinishConfigurationPacket;
import net.raphimc.netminecraft.packet.impl.configuration.S2CConfigFinishConfigurationPacket;

import java.util.Map;

public class ConfigurationStateHandler extends StateHandler {

    public ConfigurationStateHandler(final LobbyServerHandler handler, final Channel channel) {
        super(handler, channel);

        this.init();
    }

    private void init() {
        for (Map.Entry<String, CompoundTag> entry : ProtocolConstants.REGISTRIES.entrySet()) {
            this.send(new S2CConfigRegistryDataPacket(entry.getKey(), entry.getValue()));
        }
        this.send(new S2CConfigUpdateTagsPacket(ProtocolConstants.TAGS));
        this.send(new S2CConfigFinishConfigurationPacket());
    }

    @EventHandler
    public void handle(final C2SConfigFinishConfigurationPacket packet) {
        this.setState(ConnectionState.PLAY);
    }

}
