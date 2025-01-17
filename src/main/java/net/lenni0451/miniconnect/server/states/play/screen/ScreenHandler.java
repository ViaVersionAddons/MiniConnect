package net.lenni0451.miniconnect.server.states.play.screen;

import com.viaversion.viaversion.api.minecraft.item.StructuredItem;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.server.protocol.packets.play.c2s.C2SContainerClickPacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.c2s.C2SContainerClosePacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2CContainerClosePacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2CContainerSetContentPacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2COpenScreenPacket;
import net.lenni0451.miniconnect.server.states.StateHandler;

public class ScreenHandler {

    private final StateHandler stateHandler;
    private Screen currentScreen;
    private ItemList currentItemList;

    public ScreenHandler(final StateHandler stateHandler) {
        this.stateHandler = stateHandler;
    }

    public StateHandler getStateHandler() {
        return this.stateHandler;
    }

    public void openScreen(final Screen screen) {
        if (this.currentScreen != null) this.stateHandler.getHandlerManager().unregister(this.currentScreen);
        this.currentItemList = new ItemList(screen.getSlotCount());
        screen.init(this, this.currentItemList);
        this.stateHandler.send(new S2COpenScreenPacket(1, screen.getInventoryType(), screen.getTitle()));
        this.stateHandler.send(new S2CContainerSetContentPacket(1, 0, this.currentItemList.getItems(), StructuredItem.empty()));
        this.currentScreen = screen;
        this.stateHandler.getHandlerManager().register(this.currentScreen);
    }

    public void closeScreen() {
        if (this.currentScreen == null) return;
        this.stateHandler.getHandlerManager().unregister(this.currentScreen);
        this.currentScreen = null;
        this.stateHandler.send(new S2CContainerClosePacket(1));
    }

    @EventHandler
    public void handle(final C2SContainerClickPacket packet) {
        if (this.currentScreen == null) return;
        this.stateHandler.send(new S2CContainerSetContentPacket(1, 0, this.currentItemList.getItems(), StructuredItem.empty()));
        if (packet.button == 0 && packet.slot >= 0 && packet.slot < this.currentItemList.getItems().length) {
            ItemList.ClickListener listener = this.currentItemList.getListeners()[packet.slot];
            if (listener != null) listener.onClick();
        }
    }

    @EventHandler
    public void handle(final C2SContainerClosePacket packet) {
        if (this.currentScreen == null) return;
        Screen currentScreen = this.currentScreen;
        this.stateHandler.getHandlerManager().unregister(this.currentScreen);
        this.currentScreen = null; //First set the screen to null because the close logic could open a new screen
        currentScreen.close(this);
    }

}
