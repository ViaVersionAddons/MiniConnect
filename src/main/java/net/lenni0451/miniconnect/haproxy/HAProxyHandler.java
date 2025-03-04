package net.lenni0451.miniconnect.haproxy;

import io.netty.channel.AbstractChannel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.haproxy.HAProxyCommand;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.codec.haproxy.HAProxyTLV;
import net.lenni0451.miniconnect.model.AttributeKeys;
import net.lenni0451.miniconnect.model.HandshakeData;
import net.lenni0451.reflect.stream.RStream;

import java.net.InetSocketAddress;

public class HAProxyHandler extends SimpleChannelInboundHandler<HAProxyMessage> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HAProxyMessage message) throws Exception {
        if (message.command() != HAProxyCommand.PROXY) {
            throw new UnsupportedOperationException("Unsupported HAProxy command: " + message.command());
        }
        if (message.sourceAddress() != null) {
            final InetSocketAddress sourceAddress = new InetSocketAddress(message.sourceAddress(), message.sourcePort());
            if (ctx.channel() instanceof AbstractChannel) {
                RStream.of(AbstractChannel.class, ctx.channel()).fields().by("remoteAddress").set(sourceAddress);
            }
        }
        boolean hasHandshakeData = false;
        for (HAProxyTLV tlv : message.tlvs()) {
            if (tlv.typeByteValue() == (byte) 0xE0) {
                ctx.channel().attr(AttributeKeys.HANDSHAKE_DATA).set(HandshakeData.read(tlv.content()));
                hasHandshakeData = true;
            }
        }
        if (!hasHandshakeData) {
            throw new IllegalStateException("No handshake data found in HAProxy message");
        }

        ctx.pipeline().remove(this);
        super.channelActive(ctx);
    }

}
