package net.lenni0451.miniconnect;

import net.lenni0451.classtransform.TransformerManager;
import net.lenni0451.classtransform.utils.tree.BasicClassProvider;
import net.lenni0451.miniconnect.proxy.StateRegistry;
import net.lenni0451.miniconnect.proxy.event.*;
import net.lenni0451.miniconnect.server.LobbyServerInitializer;
import net.lenni0451.reflect.Agents;
import net.raphimc.netminecraft.netty.connection.NetServer;
import net.raphimc.viaproxy.ViaProxy;
import net.raphimc.viaproxy.plugins.ViaProxyPlugin;
import net.raphimc.viaproxy.util.logging.Logger;

import java.net.InetSocketAddress;

public class Main extends ViaProxyPlugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }


    private StateRegistry stateRegistry;
    private NetServer lobbyServer;

    public Main() {
        instance = this;
    }

    public NetServer getLobbyServer() {
        return this.lobbyServer;
    }

    public StateRegistry getStateRegistry() {
        return this.stateRegistry;
    }

    @Override
    public void onEnable() {
        this.inject();
        this.stateRegistry = new StateRegistry();
        this.lobbyServer = new NetServer(new LobbyServerInitializer());
        this.lobbyServer.bind(new InetSocketAddress("localhost", 0), false);

        ViaProxy.EVENT_MANAGER.register(new RedirectionHandler());
        ViaProxy.EVENT_MANAGER.register(new PacketHandlerRegistry());
        ViaProxy.EVENT_MANAGER.register(new ViaLoadHandler());
        ViaProxy.EVENT_MANAGER.register(new HAProxyEnableHandler());
        ViaProxy.EVENT_MANAGER.register(new TargetOnlineModeHandler());
        ViaProxy.EVENT_MANAGER.register(new ProxyOnlineModeHandler());
        ViaProxy.EVENT_MANAGER.register(new HandshakeIntentListener());
    }

    private void inject() {
        try {
            TransformerManager transformerManager = new TransformerManager(new BasicClassProvider(Main.class.getClassLoader()));
            transformerManager.addTransformer("net.lenni0451.miniconnect.injection.Proxy2ServerChannelInitializerTransformer");
            transformerManager.hookInstrumentation(Agents.getInstrumentation());
        } catch (Throwable t) {
            Logger.LOGGER.error("Failed to inject MiniConnect transformers", t);
            Logger.LOGGER.warn("Some features may not be working correctly");
        }
    }

}
