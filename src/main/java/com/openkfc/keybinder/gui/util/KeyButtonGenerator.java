package com.openkfc.keybinder.gui.util;

import com.openkfc.keybinder.gui.KeyButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public final class KeyButtonGenerator {
    private final GuiScreen screen;
    private final int keyScale, keyGap;
    private final List<? super KeyButton> outList;

    public KeyButtonGenerator(
            GuiScreen screen, int keyScale, int keyGap, List<? super KeyButton> outList
    ) {
        this.screen = screen;
        this.keyScale = keyScale;
        this.keyGap = keyGap;
        this.outList = outList;
    }

    public LineKeyButtonGenerator newLine(int startX, int y) {
        return new LineKeyButtonGenerator(screen, startX, y, keyScale, keyGap, outList);
    }

}
