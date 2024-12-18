package net.lenni0451.miniconnect.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import net.lenni0451.miniconnect.server.protocol.LobbyPacketRegistry;
import net.raphimc.netminecraft.constants.MCPipeline;
import net.raphimc.netminecraft.netty.connection.MinecraftChannelInitializer;
import net.raphimc.viaproxy.proxy.client2proxy.HAProxyHandler;

import java.util.function.Supplier;

public class LobbyServerInitializer extends MinecraftChannelInitializer {

    public LobbyServerInitializer(final Supplier<ChannelHandler> handlerSupplier) {
        super(handlerSupplier);
    }

    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline().addLast("haproxy-decoder", new HAProxyMessageDecoder());
        channel.pipeline().addLast("haproxy-handler", new HAProxyHandler());
        super.initChannel(channel);
        channel.attr(MCPipeline.PACKET_REGISTRY_ATTRIBUTE_KEY).set(new LobbyPacketRegistry());
    }

}
