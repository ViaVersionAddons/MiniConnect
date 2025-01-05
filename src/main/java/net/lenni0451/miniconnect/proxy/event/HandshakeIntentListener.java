package net.lenni0451.miniconnect.proxy.event;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.Main;
import net.lenni0451.miniconnect.utils.ChannelUtils;
import net.raphimc.netminecraft.constants.IntendedState;
import net.raphimc.netminecraft.constants.MCPipeline;
import net.raphimc.netminecraft.packet.Packet;
import net.raphimc.netminecraft.packet.impl.handshaking.C2SHandshakingClientIntentionPacket;
import net.raphimc.viaproxy.plugins.events.Client2ProxyChannelInitializeEvent;
import net.raphimc.viaproxy.plugins.events.types.ITyped;

public class HandshakeIntentListener {

    @EventHandler
    public void onClient2ProxyChannelInitialize(final Client2ProxyChannelInitializeEvent event) {
        if (!event.getType().equals(ITyped.Type.POST)) return;
        event.getChannel().pipeline().addBefore(MCPipeline.HANDLER_HANDLER_NAME, "miniconnect-intent-changer", new SimpleChannelInboundHandler<Packet>() {
            @Override
            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
                if (packet instanceof C2SHandshakingClientIntentionPacket clientIntention) {
                    if (clientIntention.intendedState.equals(IntendedState.TRANSFER)) {
                        if (Main.getInstance().getStateRegistry().getChangeHandshakeIntent().remove(ChannelUtils.getChannelAddress(event.getChannel()))) {
                            clientIntention.intendedState = IntendedState.LOGIN;
                        }
                    }
                }
                channelHandlerContext.fireChannelRead(packet);
            }
        });
    }

}
