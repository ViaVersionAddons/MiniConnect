package net.lenni0451.miniconnect.proxy.packet;

import io.netty.channel.ChannelFutureListener;
import net.lenni0451.miniconnect.Main;
import net.lenni0451.miniconnect.model.AttributeKeys;
import net.lenni0451.miniconnect.model.ConnectionInfo;
import net.lenni0451.miniconnect.utils.ChannelUtils;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.impl.common.S2CTransferPacket;
import net.raphimc.viaproxy.proxy.packethandler.PacketHandler;
import net.raphimc.viaproxy.proxy.session.ProxyConnection;

import java.util.List;

public class ReconnectPacketHandler extends PacketHandler {

    public ReconnectPacketHandler(final ProxyConnection proxyConnection) {
        super(proxyConnection);
    }

    @Override
    public boolean handleP2S(Packet packet, List<ChannelFutureListener> listeners) {
        if (!(packet instanceof S2CTransferPacket)) return true;
        ConnectionInfo connectionInfo = this.proxyConnection.getC2P().attr(AttributeKeys.CONNECTION_INFO).get();
        if (connectionInfo != null) { //The connection info is null if the player is not connected to a server
            Main.getInstance().getStateRegistry().getReconnectTargets().put(ChannelUtils.getChannelAddress(this.proxyConnection.getC2P()), connectionInfo);
        }
        return true;
    }

}
