package net.lenni0451.miniconnect.server.states;

import io.netty.channel.Channel;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.server.LobbyServerHandler;
import net.raphimc.netminecraft.constants.ConnectionState;
import net.raphimc.netminecraft.packet.impl.login.C2SLoginAcknowledgedPacket;
import net.raphimc.netminecraft.packet.impl.login.C2SLoginHelloPacket;
import net.raphimc.netminecraft.packet.impl.login.S2CLoginGameProfilePacket;

import java.util.ArrayList;

public class LoginStateHandler extends StateHandler {

    public LoginStateHandler(final LobbyServerHandler handler, final Channel channel) {
        super(handler, channel);
    }

    @EventHandler
    public void handle(final C2SLoginHelloPacket packet) {
        this.handler.loadPlayerConfig(this.channel, packet.uuid);
        this.send(new S2CLoginGameProfilePacket(packet.uuid, packet.name, new ArrayList<>()));
    }

    @EventHandler
    public void handle(final C2SLoginAcknowledgedPacket packet) {
        this.setState(ConnectionState.CONFIGURATION);
    }

}
