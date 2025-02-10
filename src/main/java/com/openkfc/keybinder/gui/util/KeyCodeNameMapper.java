package com.openkfc.keybinder.gui.util;

public final class KeyCodeNameMapper {

    public static final String[] KEY_NAME_ARR = new String[]{
            "_None", "Esc", "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "0", "-", "=", "Back", "Tab",
            "Q", "W", "E", "R", "T", "Y", "U", "I",
            "O", "P", "[", "]", "Enter", "LCtrl", "A", "S",
            "D", "F", "G", "H", "J", "K", "L", ";",
            "'", "`", "LShift", "\\", "Z", "X", "C", "V",
            "B", "N", "M", ",", ".", "/", "RShift", "*",
            "LAlt", "Space", "Caps", "F1", "F2", "F3", "F4", "F5",
            "F6", "F7", "F8", "F9", "F10", "NumLock", "ScrLock", "7",
            "8", "9", "-", "4", "5", "6", "+", "1",
            "2", "3", "0", ".", "", "", "", "F11",
            "F12", "", "", "", "", "", "", "",
            "", "", "", "", "F13", "F14", "F15", "F16",
            "F17", "F18", "", "", "", "", "", "",
            "KANA", "F19", "", "", "", "", "", "",
            "", "CONVERT", "", "NOCONVERT", "", "YEN", "", "",
            "", "", "", "", "", "", "", "",
            "", "", "", "", "", "=", "", "",
            "CIRCUMFLEX", "AT", "COLON", "UNDERLINE", "KANJI", "STOP", "AX", "UNLABELED",
            "", "", "", "", "Enter", "RCtrl", "", "",
            "", "", "", "", "", "", "", "SECTION",
            "", "", "", "", "", "", "", "",
            "", "", "", ",", "", "/", "", "SysRq",
            "RAlt", "", "", "", "", "", "", "",
            "", "", "", "", "Func", "Pause", "", "Home",
            "↑", "PgUp", "", "←", "→", "", "", "End",
            "↓", "PgDown", "Insert", "Del", "", "", "", "",
            "", "", "Clear", "LWin", "RWin", "Menu", "Power", "Sleep"
    };

    public static String keyCodeToStr(int keyCode) {
        try {
            return KEY_NAME_ARR[keyCode];
        } catch (NullPointerException | ArrayIndexOutOfBoundsException ignored) {}
        return "";
    }

}
