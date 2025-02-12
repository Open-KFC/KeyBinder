package com.openkfc.keybinder.gui.util;

import com.openkfc.keybinder.gui.KeyButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

import static org.lwjgl.input.Keyboard.*;

public class AlphanumericKeyButtonsInitializer extends KeyButtonsInitializer {
    public AlphanumericKeyButtonsInitializer(int left, int right, int top, int maxHeight) {
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
        int escFuncGap, funcGap;
        int wRBackspace, wLTab, wRBackSlash, wLCaps, wREnter, wLShift, wRShift, wLCtrl, wSpace, wRCtrl;
        {
            int tmp = width - 2 * around - 13 * keyScale - 8 * keyGap;
            funcGap = tmp / 4;
            escFuncGap = tmp - 3 * funcGap;
            wRBackspace = width - 2 * around - 13 * keyScale - 13 * keyGap;
            tmp = width - 2 * around - 12 * keyScale - 13 * keyGap;
            wLTab = tmp / 2;
            wRBackSlash = tmp - wLTab;
            tmp = width - 2 * around - 11 * keyScale - 12 * keyGap;
            wLCaps = tmp / 2;
            wREnter = tmp - wLCaps;
            tmp = width - 2 * around - 10 * keyScale - 11 * keyGap;
            wLShift = tmp / 2;
            wRShift = tmp - wLShift;
            wLCtrl = wLShift - keyScale - keyGap;
            wSpace = 7 * keyScale + 6 * keyGap;
            wRCtrl = wRShift - keyScale - keyGap;
        }
        int startX = x + around;
        KeyButtonGenerator kbg = new KeyButtonGenerator(screenIn, keyScale, keyGap, outList);
        //ln1: esc & funcs
        {
            LineKeyButtonGenerator lkbg = kbg.newLine(startX, yLn[0]);
            lkbg.generateG(KEY_ESCAPE, escFuncGap);
            lkbg.generate(KEY_F1, KEY_F2);
            lkbg.generateG(KEY_F3, funcGap);
            lkbg.generate(KEY_F4, KEY_F5);
            lkbg.generateG(KEY_F6, funcGap);
            lkbg.generate(KEY_F7, KEY_F8);
            lkbg.generateG(KEY_F9, funcGap);
            lkbg.generate(KEY_F10, KEY_F11);
            lkbg.generateG(KEY_F12, funcGap);
        }
        //ln2: `...backspace
        {
            LineKeyButtonGenerator lkbg = kbg.newLine(startX, yLn[1]);
            lkbg.generate(KEY_GRAVE, KEY_1, KEY_2, KEY_3, KEY_4, KEY_5, KEY_6, KEY_7, KEY_8, KEY_9, KEY_0, KEY_MINUS,
                    KEY_EQUALS
            );
            lkbg.generateW(KEY_BACK, wRBackspace);
        }
        //ln3: tab...backslash
        {
            LineKeyButtonGenerator lkbg = kbg.newLine(startX, yLn[2]);
            lkbg.generateW(KEY_TAB, wLTab);
            lkbg.generate(KEY_Q, KEY_W, KEY_E, KEY_R, KEY_T, KEY_Y, KEY_U, KEY_I, KEY_O, KEY_P, KEY_LBRACKET,
                    KEY_RBRACKET
            );
            lkbg.generateW(KEY_BACKSLASH, wRBackSlash);
        }
        //ln4: caps...enter
        {
            LineKeyButtonGenerator lkbg = kbg.newLine(startX, yLn[3]);
            lkbg.generateW(KEY_CAPITAL, wLCaps);
            lkbg.generate(KEY_A, KEY_S, KEY_D, KEY_F, KEY_G, KEY_H, KEY_J, KEY_K, KEY_L, KEY_SEMICOLON, KEY_APOSTROPHE);
            lkbg.generateW(KEY_RETURN, wREnter);
        }
        //ln5: shift...shift
        {
            LineKeyButtonGenerator lkbg = kbg.newLine(startX, yLn[4]);
            lkbg.generateW(KEY_LSHIFT, wLShift);
            lkbg.generate(KEY_Z, KEY_X, KEY_C, KEY_V, KEY_B, KEY_N, KEY_M, KEY_COMMA, KEY_PERIOD, KEY_SLASH);
            lkbg.generateW(KEY_RSHIFT, wRShift);
        }
        //ln6: ctrl...ctrl
        {
            LineKeyButtonGenerator lkbg = kbg.newLine(startX, yLn[5]);
            lkbg.generateW(KEY_LCONTROL, wLCtrl);
            lkbg.generate(KEY_LMETA, KEY_LMENU);
            lkbg.generateW(KEY_SPACE, wSpace);
            lkbg.generate(KEY_RMENU, KEY_APPS, KEY_RMETA);
            lkbg.generateW(KEY_RCONTROL, wRCtrl);
        }
    }

}
