package net.lenni0451.miniconnect.server.states.play.screen.impl;

import net.lenni0451.mcstructs.text.components.StringComponent;
import net.lenni0451.miniconnect.server.states.play.screen.ItemList;
import net.lenni0451.miniconnect.server.states.play.screen.Items;
import net.lenni0451.miniconnect.server.states.play.screen.Screen;
import net.lenni0451.miniconnect.server.states.play.screen.ScreenHandler;
import net.raphimc.netminecraft.packet.impl.play.S2CPlayDisconnectPacket;

import static net.lenni0451.miniconnect.server.states.play.screen.ItemBuilder.item;

public class MainScreen extends Screen {

    public MainScreen() {
        super(new StringComponent("§aMiniConnect"), 3);
    }

    @Override
    public void init(ScreenHandler screenHandler, ItemList itemList) {
        itemList.setItem(26, item(Items.BARRIER).named(new StringComponent("§cDisconnect")).get(), () -> {
            screenHandler.getStateHandler().send(new S2CPlayDisconnectPacket(new StringComponent("Manual Disconnect")));
        });
    }

    @Override
    public void close(ScreenHandler screenHandler) {
        //Count closing the screen as a disconnect
        screenHandler.getStateHandler().send(new S2CPlayDisconnectPacket(new StringComponent("Manual Disconnect")));
    }

}
