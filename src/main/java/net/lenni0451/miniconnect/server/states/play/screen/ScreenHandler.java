package net.lenni0451.miniconnect.server.states.play.screen;

import com.viaversion.viaversion.api.minecraft.item.StructuredItem;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.protocol.packets.play.c2s.C2SContainerClickPacket;
import net.lenni0451.miniconnect.protocol.packets.play.c2s.C2SContainerClosePacket;
import net.lenni0451.miniconnect.protocol.packets.play.s2c.S2CContainerSetContentPacket;
import net.lenni0451.miniconnect.protocol.packets.play.s2c.S2COpenScreenPacket;
import net.lenni0451.miniconnect.server.states.PlayStateHandler;

public class ScreenHandler {

    private final PlayStateHandler stateHandler;
    private Screen currentScreen;
    private ItemList currentItemList;

    public ScreenHandler(final PlayStateHandler stateHandler) {
        this.stateHandler = stateHandler;
    }

    public PlayStateHandler getStateHandler() {
        return this.stateHandler;
    }

    public void openScreen(final Screen screen) {
        this.currentItemList = new ItemList(screen.getRows() * 9);
        screen.init(this, this.currentItemList);
        this.stateHandler.send(new S2COpenScreenPacket(1, screen.getRows() - 1, screen.getTitle()));
        this.stateHandler.send(new S2CContainerSetContentPacket(1, 0, this.currentItemList.getItems(), StructuredItem.empty()));
        this.currentScreen = screen;
    }

    @EventHandler
    public void handle(final C2SContainerClickPacket packet) {
        this.stateHandler.send(new S2CContainerSetContentPacket(1, 0, this.currentItemList.getItems(), StructuredItem.empty()));
        if (packet.button == 0 && packet.slot >= 0 && packet.slot < this.currentItemList.getItems().length) {
            ItemList.ClickListener listener = this.currentItemList.getListeners()[packet.slot];
            if (listener != null) listener.onClick();
        }
    }

    @EventHandler
    public void handle(final C2SContainerClosePacket packet) {
        Screen currentScreen = this.currentScreen;
        this.currentScreen = null; //First set the screen to null because the close logic could open a new screen
        currentScreen.close(this);
    }

}
