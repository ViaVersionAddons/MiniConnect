package net.lenni0451.miniconnect.server.states.play.screen.impl;

import com.viaversion.viaversion.api.minecraft.data.StructuredDataKey;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.item.data.FilterableString;
import com.viaversion.viaversion.api.minecraft.item.data.WrittenBook;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.mcstructs.text.components.StringComponent;
import net.lenni0451.miniconnect.server.protocol.packets.play.c2s.C2SContainerButtonClickPacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2CContainerSetDataPacket;
import net.lenni0451.miniconnect.server.states.play.Tutorial;
import net.lenni0451.miniconnect.server.states.play.screen.*;

import static net.lenni0451.miniconnect.server.states.play.screen.ItemBuilder.item;

public class TutorialScreen extends Screen {

    private ScreenHandler screenHandler;
    private int currentPage;

    public TutorialScreen() {
        super(new StringComponent(Messages.TutorialScreen.Title), 17, 1);
    }

    @Override
    public void init(ScreenHandler screenHandler, ItemList itemList) {
        this.screenHandler = screenHandler;
        Item tutorialBook = item(Items.WRITTEN_BOOK).data(
                StructuredDataKey.WRITTEN_BOOK_CONTENT,
                new WrittenBook(new FilterableString("Tutorial", null), "MiniConnect", 0, Tutorial.TEXT, true)
        ).get();
        itemList.add(tutorialBook);
    }

    @Override
    public void close(ScreenHandler screenHandler) {
        screenHandler.openScreen(new MainScreen());
    }

    @EventHandler
    public void handle(final C2SContainerButtonClickPacket packet) {
        if (packet.buttonId == 1) this.currentPage--;
        else if (packet.buttonId == 2) this.currentPage++;
        if (this.currentPage < 0) this.currentPage = 0;
        if (this.currentPage >= Tutorial.TEXT.length) this.currentPage = Tutorial.TEXT.length - 1;
        this.screenHandler.getStateHandler().send(new S2CContainerSetDataPacket(packet.syncId, 0, this.currentPage));
    }

}
