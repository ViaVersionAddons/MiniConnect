package net.lenni0451.miniconnect.server.states.play.screen;

import com.viaversion.viabackwards.protocol.v1_21_4to1_21_2.Protocol1_21_4To1_21_2;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.data.StructuredDataKey;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.item.StructuredItem;
import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.miniconnect.protocol.ProtocolConstants;
import net.lenni0451.miniconnect.utils.ViaUtils;

public class ItemBuilder {

    public static ItemBuilder item(final String id) {
        return new ItemBuilder(id);
    }


    private final String id;
    private ATextComponent name;

    private ItemBuilder(final String id) {
        this.id = id;
    }

    public ItemBuilder named(final ATextComponent name) {
        this.name = name;
        return this;
    }

    public Item get() {
        StructuredItem item = new StructuredItem(ProtocolConstants.ITEMS.indexOf(this.id), 1);
        item.dataContainer().setIdLookup(Via.getManager().getProtocolManager().getProtocol(Protocol1_21_4To1_21_2.class), false);
        if (this.name != null) {
            item.dataContainer().set(StructuredDataKey.CUSTOM_NAME, ViaUtils.convertNbt(ProtocolConstants.TEXT_CODEC.serializeNbt(this.name)));
        }
        return item;
    }

}
