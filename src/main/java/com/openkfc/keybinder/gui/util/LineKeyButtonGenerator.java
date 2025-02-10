package com.openkfc.keybinder.gui.util;

import com.openkfc.keybinder.gui.KeyButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

import static com.openkfc.keybinder.gui.util.KeyCodeNameMapper.keyCodeToStr;

public final class LineKeyButtonGenerator {
    private final GuiScreen screen;
    private int currentX;
    private final int y;
    private final int keyScale, keyGap;
    private final List<? super KeyButton> outList;

    public LineKeyButtonGenerator(
            GuiScreen screen, int startX, int y, int keyScale, int keyGap,
            List<? super KeyButton> outList
    ) {
        this.screen = screen;
        currentX = startX;
        this.y = y;
        this.keyScale = keyScale;
        this.keyGap = keyGap;
        this.outList = outList;
    }

    public int getCurrentX() {return currentX;}

    public void generate(int keyCode) {
        generateWG(keyCode, keyScale, keyGap);
    }

    public void generateG(int keyCode, int gap) {
        generateWG(keyCode, keyScale, gap);
    }

    public void generateW(int keyCode, int width) {
        generateWG(keyCode, width, keyGap);
    }

    public void generateWG(int keyCode, int width, int gap) {
        outList.add(new KeyButton(screen, keyCode, currentX, y, width, keyScale, keyCodeToStr(keyCode)));
        currentX += width + gap;
    }

    public void generate(int... keyCodes) {
        for (int keyCode : keyCodes)
            generate(keyCode);
    }

}
