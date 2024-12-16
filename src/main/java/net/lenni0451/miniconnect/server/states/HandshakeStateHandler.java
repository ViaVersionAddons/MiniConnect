package net.lenni0451.miniconnect.server.states;

import io.netty.channel.Channel;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.protocol.ProtocolConstants;
import net.lenni0451.miniconnect.server.LobbyServerHandler;
import net.raphimc.netminecraft.packet.impl.handshaking.C2SHandshakingClientIntentionPacket;

public class HandshakeStateHandler extends StateHandler {

    public HandshakeStateHandler(final LobbyServerHandler handler, final Channel channel) {
        super(handler, channel);
    }

    @EventHandler
    public void handle(final C2SHandshakingClientIntentionPacket packet) {
        if (packet.protocolVersion != ProtocolConstants.PROTOCOL_VERSION.getVersion()) {
            this.channel.close();
            return;
        }
        this.handler.getPlayerConfig().handshakeAddress = packet.address;
        this.handler.getPlayerConfig().handshakePort = packet.port;
        this.setState(packet.intendedState.getConnectionState());
    }

}
