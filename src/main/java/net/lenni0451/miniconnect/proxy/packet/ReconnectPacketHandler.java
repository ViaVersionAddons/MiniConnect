package net.lenni0451.miniconnect.proxy.packet;

import io.netty.channel.ChannelFutureListener;
import net.lenni0451.miniconnect.proxy.TransferHandler;
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
        TransferHandler.handle(this.proxyConnection.getC2P());
        return true;
    }

}
