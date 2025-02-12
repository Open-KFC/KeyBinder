package com.openkfc.keybinder.gui.util;

import com.openkfc.keybinder.gui.KeyButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public abstract class KeyButtonsInitializer {
    public final int x, y, width, height;

    public KeyButtonsInitializer(int left, int right, int top, int maxHeight) {
        heightLimit:
        {
            if (maxHeight == -1)
                break heightLimit;
            else if (maxHeight <= 0)
                throw new IllegalArgumentException(String.format("MaxHeight(%d) must be positive!", maxHeight));
            int maxWidth = (int) (maxHeight / 0.4166667f);
            if (right - left < maxWidth)
                break heightLimit;
            int tmp = (right - left) - maxWidth;
            left += tmp >> 1;
            right = left + maxWidth;
        }
        x = left;
        width = right - left;
        height = (int) (width * 0.4166667f); // 12 : 5
        y = top;
    }

    public abstract void applyTo(GuiScreen screenIn, List<? super KeyButton> outList);

}
