package net.lenni0451.miniconnect.server.states.play.screen.impl;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.lenni0451.mcstructs.text.components.StringComponent;
import net.lenni0451.miniconnect.server.model.PlayerConfig;
import net.lenni0451.miniconnect.server.states.play.screen.ItemList;
import net.lenni0451.miniconnect.server.states.play.screen.Items;
import net.lenni0451.miniconnect.server.states.play.screen.Screen;
import net.lenni0451.miniconnect.server.states.play.screen.ScreenHandler;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.lenni0451.miniconnect.server.states.play.screen.ItemBuilder.item;

public class VersionSelectorScreen extends Screen {

    private final int page;

    public VersionSelectorScreen(final int page) {
        super(new StringComponent("§aSelect Version"), 6);
        this.page = page;
    }

    @Override
    public void init(ScreenHandler screenHandler, ItemList itemList) {
        PlayerConfig playerConfig = screenHandler.getStateHandler().getHandler().getPlayerConfig();

        List<ProtocolVersion> versions = new ArrayList<>(ProtocolVersion.getProtocols());
        Collections.reverse(versions);
        versions = versions.subList(this.page * 45, Math.min(versions.size(), (this.page + 1) * 45));
        for (ProtocolVersion version : versions) {
            String item;
            if (version == BedrockProtocolVersion.bedrockLatest) {
                item = Items.BEDROCK;
            } else if (version.newerThanOrEqualTo(LegacyProtocolVersion.r1_0_0tor1_0_1)) {
                item = Items.CRAFTING_TABLE;
            } else if (version.newerThanOrEqualTo(LegacyProtocolVersion.b1_0tob1_1_1)) {
                item = Items.FURNACE;
            } else {
                item = Items.DIRT;
            }
            itemList.add(item(item).named(new StringComponent("§a" + version.getName())).setGlint(version == playerConfig.targetVersion).get(), () -> {
                playerConfig.targetVersion = version;
                screenHandler.openScreen(new MainScreen());
            });
        }
        if (this.page == 0) {
            itemList.set(45, item(Items.GRAY_STAINED_GLASS_PANE).named(new StringComponent(" ")).get());
        } else {
            itemList.set(45, item(Items.ARROW).named(new StringComponent("§aPrevious Page")).get(), () -> {
                screenHandler.openScreen(new VersionSelectorScreen(this.page - 1));
            });
        }
        for (int i = 46; i <= 52; i++) {
            itemList.set(i, item(Items.GRAY_STAINED_GLASS_PANE).named(new StringComponent(" ")).get());
        }
        if (ProtocolVersion.getProtocols().size() > (this.page + 1) * 45) {
            itemList.set(53, item(Items.ARROW).named(new StringComponent("§aNext Page")).get(), () -> {
                screenHandler.openScreen(new VersionSelectorScreen(this.page + 1));
            });
        } else {
            itemList.set(53, item(Items.GRAY_STAINED_GLASS_PANE).named(new StringComponent(" ")).get());
        }
    }

    @Override
    public void close(ScreenHandler screenHandler) {
        screenHandler.openScreen(new MainScreen());
    }

}
