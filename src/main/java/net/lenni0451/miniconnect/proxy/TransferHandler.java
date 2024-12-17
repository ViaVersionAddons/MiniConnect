package net.lenni0451.miniconnect.proxy;

import io.netty.channel.Channel;
import net.lenni0451.miniconnect.Main;
import net.lenni0451.miniconnect.model.AttributeKeys;
import net.lenni0451.miniconnect.model.ConnectionInfo;
import net.lenni0451.miniconnect.utils.ChannelUtils;
import net.raphimc.viaproxy.proxy.session.ProxyConnection;

public class TransferHandler {

    public static void handle(Channel channel) {
        channel = ProxyConnection.fromChannel(channel).getC2P();
        ConnectionInfo connectionInfo = channel.attr(AttributeKeys.CONNECTION_INFO).get();
        if (connectionInfo != null) { //The connection info is null if the player is not connected to a server
            Main.getInstance().getStateRegistry().getReconnectTargets().put(ChannelUtils.getChannelAddress(channel), connectionInfo);
        }
    }

}
