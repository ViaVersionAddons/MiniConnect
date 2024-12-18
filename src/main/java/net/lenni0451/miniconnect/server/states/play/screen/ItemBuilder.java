package net.lenni0451.miniconnect.server.states.play.screen;

import com.viaversion.nbt.tag.Tag;
import com.viaversion.viabackwards.protocol.v1_21_4to1_21_2.Protocol1_21_4To1_21_2;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.data.StructuredDataKey;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.item.StructuredItem;
import net.lenni0451.mcstructs.text.ATextComponent;
import net.lenni0451.miniconnect.server.protocol.ProtocolConstants;
import net.lenni0451.miniconnect.utils.ViaUtils;

import java.util.*;
import java.util.function.Consumer;

public class ItemBuilder {

    public static ItemBuilder item(final String id) {
        return new ItemBuilder(id);
    }


    private final String id;
    private ATextComponent name;
    private final List<ATextComponent> lore = new ArrayList<>();
    private Boolean glint;
    private final Map<StructuredDataKey, Object> structuredData = new HashMap<>();

    private ItemBuilder(final String id) {
        this.id = id;
    }

    public ItemBuilder named(final ATextComponent name) {
        this.name = name;
        return this;
    }

    public ItemBuilder lore(final ATextComponent... lore) {
        Collections.addAll(this.lore, lore);
        return this;
    }

    public ItemBuilder setGlint(final boolean state) {
        this.glint = state;
        return this;
    }

    public <T> ItemBuilder data(final StructuredDataKey<T> key, final T value) {
        this.structuredData.put(key, value);
        return this;
    }

    public ItemBuilder calculate(final Consumer<ItemBuilder> consumer) {
        consumer.accept(this);
        return this;
    }

    public Item get() {
        int rawId = ProtocolConstants.ITEMS.indexOf(this.id);
        if (rawId == -1) throw new IllegalArgumentException("Unknown item id: " + this.id);
        StructuredItem item = new StructuredItem(rawId, 1);
        item.dataContainer().setIdLookup(Via.getManager().getProtocolManager().getProtocol(Protocol1_21_4To1_21_2.class), false);
        if (this.name != null) {
            item.dataContainer().set(StructuredDataKey.CUSTOM_NAME, ViaUtils.convertNbt(ProtocolConstants.TEXT_CODEC.serializeNbt(this.name)));
        }
        if (!this.lore.isEmpty()) {
            Tag[] lore = new Tag[this.lore.size()];
            for (int i = 0; i < this.lore.size(); i++) {
                lore[i] = ViaUtils.convertNbt(ProtocolConstants.TEXT_CODEC.serializeNbt(this.lore.get(i)));
            }
            item.dataContainer().set(StructuredDataKey.LORE, lore);
        }
        if (this.glint != null) {
            item.dataContainer().set(StructuredDataKey.ENCHANTMENT_GLINT_OVERRIDE, this.glint);
        }
        for (Map.Entry<StructuredDataKey, Object> entry : this.structuredData.entrySet()) {
            item.dataContainer().set(entry.getKey(), entry.getValue());
        }
        return item;
    }

}
