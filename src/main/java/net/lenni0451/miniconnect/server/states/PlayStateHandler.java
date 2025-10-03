package net.lenni0451.miniconnect.server.states;

import io.netty.channel.Channel;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.server.LobbyServerHandler;
import net.lenni0451.miniconnect.server.protocol.ProtocolConstants;
import net.lenni0451.miniconnect.server.protocol.packets.play.c2s.C2SChatCommandPacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.c2s.C2SChatPacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2CKeepAlivePacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2CLoginPacket;
import net.lenni0451.miniconnect.server.states.play.screen.ScreenHandler;
import net.lenni0451.miniconnect.server.states.play.screen.impl.MainScreen;

import java.util.function.Function;

public class PlayStateHandler extends StateHandler {

    private ScreenHandler screenHandler;

    public PlayStateHandler(final LobbyServerHandler handler, final Channel channel) {
        super(handler, channel);

        this.init();
    }

    private void init() {
        this.send(new S2CLoginPacket(0, false, 1, 1, 1, false, false, false, ProtocolConstants.DEFAULT_SPAWN_INFO, false));
        ProtocolConstants.sendSpawnInfo(this);

        this.screenHandler = new ScreenHandler(this);
        this.handlerManager.register(this.screenHandler);
        this.screenHandler.openScreen(new MainScreen());
    }

    @Override
    public void tick() {
        this.send(new S2CKeepAlivePacket(0));
    }

    @EventHandler
    public void handle(final C2SChatPacket packet) {
        if (this.handler.getPlayerConfig().chatListener != null) {
            Function<String, Boolean> listener = this.handler.getPlayerConfig().chatListener;
            if (listener.apply(packet.message)) {
                this.handler.getPlayerConfig().chatListener = null;
            }
        }
    }

    @EventHandler
    public void handle(final C2SChatCommandPacket packet) {
        if (this.handler.getPlayerConfig().chatListener != null) {
            Function<String, Boolean> listener = this.handler.getPlayerConfig().chatListener;
            if (listener.apply("/" + packet.message)) {
                this.handler.getPlayerConfig().chatListener = null;
            }
        }
    }

}
