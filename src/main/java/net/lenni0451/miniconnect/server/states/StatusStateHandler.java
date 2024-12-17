package net.lenni0451.miniconnect.server.states;

import io.netty.channel.Channel;
import net.lenni0451.commons.gson.elements.GsonObject;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.server.LobbyServerHandler;
import net.lenni0451.miniconnect.server.protocol.ProtocolConstants;
import net.raphimc.netminecraft.packet.impl.status.C2SStatusPingRequestPacket;
import net.raphimc.netminecraft.packet.impl.status.C2SStatusRequestPacket;
import net.raphimc.netminecraft.packet.impl.status.S2CStatusPongResponsePacket;
import net.raphimc.netminecraft.packet.impl.status.S2CStatusResponsePacket;

public class StatusStateHandler extends StateHandler {

    public StatusStateHandler(final LobbyServerHandler handler, final Channel channel) {
        super(handler, channel);
    }

    @EventHandler
    public void handle(final C2SStatusPingRequestPacket packet) {
        this.send(new S2CStatusPongResponsePacket(packet.startTime));
    }

    @EventHandler
    public void handle(final C2SStatusRequestPacket packet) {
        GsonObject response = new GsonObject();
        response.add("description", "§e§lViaProxy MiniConnect Server");
        response.add("version", new GsonObject().add("name", "all").add("protocol", ProtocolConstants.PROTOCOL_VERSION.getVersion()));
        response.add("players", new GsonObject().add("online", 0).add("max", 20));
        this.send(new S2CStatusResponsePacket(response.toString()));
    }

}
