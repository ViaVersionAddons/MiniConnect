package net.lenni0451.miniconnect.server.states;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import net.lenni0451.lambdaevents.LambdaManager;
import net.lenni0451.lambdaevents.generator.LambdaMetaFactoryGenerator;
import net.lenni0451.miniconnect.server.LobbyServerHandler;
import net.raphimc.netminecraft.constants.ConnectionState;
import net.raphimc.netminecraft.constants.MCPipeline;
import net.raphimc.netminecraft.packet.Packet;

import java.lang.invoke.MethodHandles;

public class StateHandler {

    protected final LobbyServerHandler handler;
    protected final Channel channel;
    private final LambdaManager lambdaManager;

    public StateHandler(final LobbyServerHandler handler, final Channel channel) {
        this.handler = handler;
        this.channel = channel;
        this.lambdaManager = LambdaManager.basic(new LambdaMetaFactoryGenerator(MethodHandles.lookup()));

        this.lambdaManager.register(this);
    }

    public void tick() {
    }

    public final void handle(final Packet packet) {
        this.lambdaManager.call(packet);
    }

    protected final void send(final Packet packet) {
        this.channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    protected final void setState(final ConnectionState state) {
        this.channel.attr(MCPipeline.PACKET_REGISTRY_ATTRIBUTE_KEY).get().setConnectionState(state);
        this.handler.update(this.channel, state);
    }

}
