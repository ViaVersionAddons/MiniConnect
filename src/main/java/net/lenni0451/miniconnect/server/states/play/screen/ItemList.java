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

    public void add(final Item item) {
        this.add(item, null);
    }

    public void add(final Item item, @Nullable final ClickListener clickListener) {
        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i].isEmpty()) {
                this.items[i] = item;
                this.listeners[i] = clickListener;
                return;
            }
        }
        throw new IllegalStateException("No free slot available");
    }

    public void set(final int slot, final Item item) {
        this.set(slot, item, null);
    }

    public void set(final int slot, final Item item, @Nullable final ClickListener listener) {
        this.items[slot] = item;
        this.listeners[slot] = listener;
    }


    @FunctionalInterface
    public interface ClickListener {
        void onClick();
    }

}
