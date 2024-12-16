package net.lenni0451.miniconnect.server.states.play.screen.impl;

import com.google.common.net.HostAndPort;
import net.lenni0451.mcstructs.text.components.StringComponent;
import net.lenni0451.miniconnect.Main;
import net.lenni0451.miniconnect.protocol.packets.play.s2c.S2CContainerClosePacket;
import net.lenni0451.miniconnect.protocol.packets.play.s2c.S2CSystemChatPacket;
import net.lenni0451.miniconnect.protocol.packets.play.s2c.S2CTransferPacket;
import net.lenni0451.miniconnect.server.states.play.PlayerConfig;
import net.lenni0451.miniconnect.server.states.play.screen.ItemList;
import net.lenni0451.miniconnect.server.states.play.screen.Items;
import net.lenni0451.miniconnect.server.states.play.screen.Screen;
import net.lenni0451.miniconnect.server.states.play.screen.ScreenHandler;
import net.raphimc.netminecraft.packet.impl.play.S2CPlayDisconnectPacket;

import java.net.InetSocketAddress;

import static net.lenni0451.miniconnect.server.states.play.screen.ItemBuilder.item;

public class MainScreen extends Screen {

    public MainScreen() {
        super(new StringComponent("§aMiniConnect"), 3);
    }

    @Override
    public void init(ScreenHandler screenHandler, ItemList itemList) {
        PlayerConfig playerConfig = screenHandler.getStateHandler().getPlayerConfig();
        boolean hasAddress = playerConfig.serverAddress != null;
        boolean hasVersion = playerConfig.targetVersion != null;

        itemList.set(10, item(Items.NAMETAG).named(new StringComponent("§aSet server address")).setGlint(hasAddress).get(), () -> {
            screenHandler.getStateHandler().send(new S2CContainerClosePacket(1));
            screenHandler.getStateHandler().send(new S2CSystemChatPacket(new StringComponent("§aPlease enter the server ip into the chat (with optional port) (e.g. example.com, example.com:25565"), false));
            playerConfig.chatListener = s -> {
                try {
                    HostAndPort hostAndPort = HostAndPort.fromString(s);
                    playerConfig.serverAddress = hostAndPort.getHost();
                    playerConfig.serverPort = hostAndPort.getPortOrDefault(25565); //TODO: Get the default port from the protocol?
                } catch (Throwable t) {
                    screenHandler.getStateHandler().send(new S2CSystemChatPacket(new StringComponent("§cInvalid server address"), false));
                }
                screenHandler.openScreen(this);
            };
        });
        itemList.set(12, item(Items.WRITABLE_BOOK).named(new StringComponent("§aSet protocol version")).setGlint(hasVersion).get(), () -> {
            screenHandler.openScreen(new VersionSelectorScreen(0));
        });
        itemList.set(16, item(Items.OAK_DOOR).named(new StringComponent("§a§lConnect to server")).setGlint(hasAddress && hasVersion).get(), () -> {
            if (hasAddress && hasVersion) {
                Main.getInstance().registerReconnect(screenHandler.getStateHandler().getChannel(), new InetSocketAddress(playerConfig.serverAddress, playerConfig.serverPort));
                screenHandler.getStateHandler().send(new S2CTransferPacket("127.0.0.1", 25565)); //TODO: Get the ViaProxy address and port
            } else {
                screenHandler.getStateHandler().send(new S2CSystemChatPacket(new StringComponent("§cYou need to set all options before connecting"), false));
            }
        });
        itemList.set(26, item(Items.BARRIER).named(new StringComponent("§cDisconnect")).get(), () -> {
            screenHandler.getStateHandler().send(new S2CPlayDisconnectPacket(new StringComponent("Manual Disconnect")));
        });
    }

    @Override
    public void close(ScreenHandler screenHandler) {
        //Count closing the screen as a disconnect
        if (!screenHandler.getStateHandler().getPlayerConfig().allowCloseScreen()) {
            screenHandler.getStateHandler().send(new S2CPlayDisconnectPacket(new StringComponent("Manual Disconnect")));
        }
    }

}
