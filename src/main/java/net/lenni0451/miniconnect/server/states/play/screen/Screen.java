package net.lenni0451.miniconnect.server.states.play.screen;

import net.lenni0451.mcstructs.text.ATextComponent;

public abstract class Screen {

    private final ATextComponent title;
    private final int slotCount;
    private final int inventoryType;

    public Screen(final ATextComponent title, final int rows) {
        this.title = title;
        this.slotCount = rows * 9;
        this.inventoryType = rows - 1;
    }

    public Screen(final ATextComponent title, final int inventoryType, final int slotCount) {
        this.title = title;
        this.slotCount = slotCount;
        this.inventoryType = inventoryType;
    }

    public ATextComponent getTitle() {
        return this.title;
    }

    public int getSlotCount() {
        return this.slotCount;
    }

    public int getInventoryType() {
        return this.inventoryType;
    }

    public abstract void init(final ScreenHandler screenHandler, final ItemList itemList);

    public abstract void close(final ScreenHandler screenHandler);

}
