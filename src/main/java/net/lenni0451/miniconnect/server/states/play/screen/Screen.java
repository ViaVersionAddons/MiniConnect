package net.lenni0451.miniconnect.server.states.play.screen;

import net.lenni0451.mcstructs.text.ATextComponent;

public abstract class Screen {

    private final ATextComponent title;
    private final int rows;

    public Screen(final ATextComponent title, final int rows) {
        this.title = title;
        this.rows = rows;
    }

    public ATextComponent getTitle() {
        return this.title;
    }

    public int getRows() {
        return this.rows;
    }

    public abstract void init(final ScreenHandler screenHandler, final ItemList itemList);

    public abstract void close(final ScreenHandler screenHandler);

}
