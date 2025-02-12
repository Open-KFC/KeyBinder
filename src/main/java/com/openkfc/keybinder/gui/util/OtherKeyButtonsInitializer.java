package com.openkfc.keybinder.gui.util;

import com.openkfc.keybinder.gui.KeyButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

import static org.lwjgl.input.Keyboard.*;

public class OtherKeyButtonsInitializer extends KeyButtonsInitializer {
    public OtherKeyButtonsInitializer(int left, int right, int top, int maxHeight) {
        super(left, right, top, maxHeight);
    }

    @Override public void applyTo(GuiScreen screenIn, List<? super KeyButton> outList) {
        int keyScale, keyGap, around;
        int[] yLn = new int[6];
        {
            keyGap = height / 70;
            around = height / 35;
            keyScale = (height - 2 * around - 5 * keyGap) / 6;
            yLn[0] = y + around;
            yLn[5] = y + height - around - keyScale;
            for (int i = 4; i >= 1; i--)
                yLn[i] = yLn[i + 1] - keyGap - keyScale;
        }
        int[] startX = new int[3];
        {
            int xGap = 3 * keyGap;
            startX[0] = x + around;
            startX[1] = startX[0] + 3 * keyScale + 2 * keyGap + xGap;
//            startX[2] = startX[1] + 4 * keyScale + 2 * keyGap + xGap;
            startX[2] = width - around - 3 * keyScale - 2 * keyGap;
        }
        KeyButtonGenerator kbg = new KeyButtonGenerator(screenIn, keyScale, keyGap, outList);
        //control
        {
            kbg.newLine(startX[0], yLn[0]).generate(KEY_SYSRQ, KEY_SCROLL, KEY_PAUSE);
        }
        //navigation
        {
            kbg.newLine(startX[0], yLn[1]).generate(KEY_INSERT, KEY_HOME, KEY_PRIOR);
            kbg.newLine(startX[0], yLn[2]).generate(KEY_DELETE, KEY_END, KEY_NEXT);
        }
        //arrow
        {
            kbg.newLine(startX[0] + keyScale + keyGap, yLn[4]).generate(KEY_UP);
            kbg.newLine(startX[0], yLn[5]).generate(KEY_LEFT, KEY_DOWN, KEY_RIGHT);
        }
        //numpad
        {
            kbg.newLine(startX[1], yLn[1]).generate(KEY_NUMLOCK, KEY_DIVIDE, KEY_MULTIPLY, KEY_SUBTRACT);
            LineKeyButtonGenerator lkbg = kbg.newLine(startX[1], yLn[2]);
            lkbg.generate(KEY_NUMPAD7, KEY_NUMPAD8, KEY_NUMPAD9);
            lkbg.generateWHG(KEY_ADD, keyScale, keyScale * 2 + keyGap, keyGap);
            kbg.newLine(startX[1], yLn[3]).generate(KEY_NUMPAD4, KEY_NUMPAD5, KEY_NUMPAD6);
            lkbg = kbg.newLine(startX[1], yLn[4]);
            lkbg.generate(KEY_NUMPAD1, KEY_NUMPAD2, KEY_NUMPAD3);
            lkbg.generateWHG(KEY_NUMPADENTER, keyScale, keyScale * 2 + keyGap, keyGap);
            lkbg = kbg.newLine(startX[1], yLn[5]);
            lkbg.generateW(KEY_NUMPAD0, keyScale * 2 + keyGap);
            lkbg.generate(KEY_DECIMAL);
        }
        //mouse
        {
            kbg.newLine(startX[2], yLn[1]).generate(-100, -98, -99);
            kbg.newLine(startX[2], yLn[2]).generateW(-97, 3 * keyScale + 2 * keyGap);
            kbg.newLine(startX[2], yLn[3]).generateW(-96, 3 * keyScale + 2 * keyGap);
        }
    }
}
