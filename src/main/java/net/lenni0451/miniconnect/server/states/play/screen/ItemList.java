package net.lenni0451.miniconnect.server.states.play.screen;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.item.StructuredItem;

import javax.annotation.Nullable;

public class ItemList {

    private final Item[] items;
    private final ClickListener[] listeners;

    public ItemList(final int slotCount) {
        this.items = StructuredItem.emptyArray(slotCount);
        this.listeners = new ClickListener[slotCount];
    }

    public Item[] getItems() {
        return this.items;
    }

    public ClickListener[] getListeners() {
        return this.listeners;
    }

    public void setItem(final int slot, final Item item) {
        this.setItem(slot, item, null);
    }

    public void setItem(final int slot, final Item item, @Nullable final ClickListener listener) {
        this.items[slot] = item;
        this.listeners[slot] = listener;
    }


    @FunctionalInterface
    public interface ClickListener {
        void onClick();
    }

}
