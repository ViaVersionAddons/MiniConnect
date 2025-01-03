package net.lenni0451.miniconnect.server;

import io.netty.channel.Channel;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import net.lenni0451.miniconnect.haproxy.HAProxyHandler;
import net.lenni0451.miniconnect.server.protocol.LobbyPacketRegistry;
import net.raphimc.netminecraft.constants.MCPipeline;
import net.raphimc.netminecraft.netty.connection.MinecraftChannelInitializer;

public class LobbyServerInitializer extends MinecraftChannelInitializer {

    public LobbyServerInitializer() {
        super(LobbyServerHandler::new);
    }

    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline().addLast("haproxy-decoder", new HAProxyMessageDecoder());
        channel.pipeline().addLast("haproxy-handler", new HAProxyHandler());
        super.initChannel(channel);
        channel.attr(MCPipeline.PACKET_REGISTRY_ATTRIBUTE_KEY).set(new LobbyPacketRegistry());
    }

}
