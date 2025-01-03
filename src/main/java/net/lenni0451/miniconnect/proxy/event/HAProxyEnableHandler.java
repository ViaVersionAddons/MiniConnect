package net.lenni0451.miniconnect.proxy.event;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.haproxy.HAProxyMessageEncoder;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.haproxy.HAProxyUtil;
import net.lenni0451.miniconnect.model.AttributeKeys;
import net.raphimc.viaproxy.ViaProxy;
import net.raphimc.viaproxy.plugins.events.Proxy2ServerChannelInitializeEvent;
import net.raphimc.viaproxy.plugins.events.ViaProxyLoadedEvent;
import net.raphimc.viaproxy.plugins.events.types.ITyped;
import net.raphimc.viaproxy.proxy.session.ProxyConnection;

import static net.raphimc.viaproxy.proxy.proxy2server.Proxy2ServerChannelInitializer.VIAPROXY_HAPROXY_ENCODER_NAME;

public class HAProxyEnableHandler {

    @EventHandler
    public void onViaProxyLoaded(final ViaProxyLoadedEvent events) {
        ViaProxy.getConfig().setBackendHaProxy(false);
        ViaProxy.getConfig().setAllowLegacyClientPassthrough(false);
    }

    @EventHandler
    public void onProxy2ServerChannelInitialize(final Proxy2ServerChannelInitializeEvent event) {
        if (!event.getType().equals(ITyped.Type.POST)) return;
        ProxyConnection proxyConnection = ProxyConnection.fromChannel(event.getChannel());
        if (!proxyConnection.getC2P().hasAttr(AttributeKeys.ENABLE_HAPROXY)) return;
        event.getChannel().pipeline().addFirst("miniconnect-", new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                super.channelActive(ctx);
                ctx.writeAndFlush(HAProxyUtil.createMessage(proxyConnection.getC2P(), ctx.channel(), proxyConnection.getClientHandshakeAddress(), proxyConnection.getClientVersion())).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
        });
        event.getChannel().pipeline().addFirst(VIAPROXY_HAPROXY_ENCODER_NAME, HAProxyMessageEncoder.INSTANCE);
    }

}
