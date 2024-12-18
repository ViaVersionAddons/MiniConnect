package net.lenni0451.miniconnect.server.states.play.screen.impl;

import com.viaversion.viaversion.api.minecraft.data.StructuredDataKey;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.item.data.FilterableComponent;
import com.viaversion.viaversion.api.minecraft.item.data.FilterableString;
import com.viaversion.viaversion.api.minecraft.item.data.WrittenBook;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.mcstructs.text.components.StringComponent;
import net.lenni0451.miniconnect.server.protocol.ProtocolConstants;
import net.lenni0451.miniconnect.server.protocol.packets.play.c2s.C2SContainerButtonClickPacket;
import net.lenni0451.miniconnect.server.protocol.packets.play.s2c.S2CContainerSetDataPacket;
import net.lenni0451.miniconnect.server.states.play.screen.ItemList;
import net.lenni0451.miniconnect.server.states.play.screen.Items;
import net.lenni0451.miniconnect.server.states.play.screen.Screen;
import net.lenni0451.miniconnect.server.states.play.screen.ScreenHandler;
import net.lenni0451.miniconnect.utils.ViaUtils;

import java.util.ArrayList;
import java.util.List;

import static net.lenni0451.miniconnect.server.states.play.screen.ItemBuilder.item;

public class TutorialScreen extends Screen {

    private static final FilterableComponent[] TUTORIAL = buildTutorial(
            """
                    §6§l§oMiniConnect
                    
                    Using MiniConnect you can connect to any Minecraft server with any version.
                    Check out the following pages to learn how to use MiniConnect.
                    """,
            """
                    §6§l§oServer address
                    
                    Click on §2Set server address§r (name tag) and enter the server address in the chat.
                    Example: §2example.com§r or §2example.com:25565§r
                    If no port is specified, the default port is used.
                    """,
            """
                    §6§l§oServer version
                    
                    Click on §2Set protocol version§r (anvil) and select the desired version.
                    §1Crafting table -> release version
                    §2Furnace -> beta version
                    §3Dirt -> alpha version
                    §4Bedrock -> bedrock edition
                    """,
            """
                    §6§l§oLogin
                    
                    Click on §2Login§r (key) to log in with your Minecraft account.
                    Click on the URL in the chat and complete the login process.
                    After disconnecting, the account will be logged out.
                    """,
            """
                    §6§l§oDisconnect
                    
                    When on a server, you can type §2/disconnect§r in the chat to disconnect.
                    After disconnecting, you will automatically be placed into the lobby again.
                    All your settings will be retained until you disconnect.
                    """
    );

    private static FilterableComponent[] buildTutorial(final String... pages) {
        List<FilterableComponent> components = new ArrayList<>();
        for (String page : pages) {
            String[] lines = page.trim().split("\n");
            StringComponent base = new StringComponent();
            for (int i = 0; i < lines.length; i++) {
                if (i != 0) base.append("\n");
                base.append(lines[i]);
            }
            components.add(new FilterableComponent(ViaUtils.convertNbt(ProtocolConstants.TEXT_CODEC.serializeNbt(base)), null));
        }
        return components.toArray(new FilterableComponent[0]);
    }


    private ScreenHandler screenHandler;
    private int currentPage;

    public TutorialScreen() {
        super(new StringComponent("MiniConnect Tutorial"), 17, 1);
    }

    @Override
    public void init(ScreenHandler screenHandler, ItemList itemList) {
        this.screenHandler = screenHandler;
        Item tutorialBook = item(Items.WRITTEN_BOOK).data(
                StructuredDataKey.WRITTEN_BOOK_CONTENT,
                new WrittenBook(new FilterableString("Tutorial", null), "MiniConnect", 0, TUTORIAL, true)
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
        if (this.currentPage >= TUTORIAL.length) this.currentPage = TUTORIAL.length - 1;
        this.screenHandler.getStateHandler().send(new S2CContainerSetDataPacket(packet.syncId, 0, this.currentPage));
    }

}
