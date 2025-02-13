package com.openkfc.keybinder.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.util.*;

public class MyKeyBindingList extends GuiListExtended {
    public interface IKeyBindingListContainer {
        KeyBinding getSelectingKeyBinding();

        void setSelectingKeyBinding(KeyBinding kb);

        void onKeyBindingChanged();
    }

    protected IKeyBindingListContainer container;
    protected List<KeyBinding> keyBindings;
    protected final IGuiListEntry[] entries;
    protected int maxLabelTextWidth;
    protected long lastClickTime;

    public MyKeyBindingList(
            IKeyBindingListContainer container, Minecraft mcIn, Collection<? extends KeyBinding> keyBindingsIn,
            int widthIn, int heightIn, int topIn, int bottomIn
    ) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, 20);
        this.container = container;
        keyBindings = new ArrayList<>(keyBindingsIn);
        keyBindings.sort(KeyBinding::compareTo);
        List<IGuiListEntry> entryList = new ArrayList<>();
        String curCatrgory = null;
        for (KeyBinding kb : keyBindings) {
            String category = kb.getKeyCategory();
            if (!Objects.equals(category, curCatrgory)) {
                curCatrgory = category;
                entryList.add(new CategoryEntry(category));
            }
            int strWidth = mc.fontRenderer.getStringWidth(I18n.format(kb.getKeyDescription()));
            if (strWidth > maxLabelTextWidth)
                maxLabelTextWidth = strWidth;
            entryList.add(new KeyEntry(kb));
        }
        entries = entryList.toArray(new IGuiListEntry[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override public IGuiListEntry getListEntry(int index) {return entries[index];}

    @Override protected int getSize() {return entries.length;}

    protected int getScrollBarX() {return super.getScrollBarX() + 35;}

    public int getListWidth() {return super.getListWidth() + 32;}

    /**
     * call before super.handleKeyboardInput()
     */
    public void handleKeyboardInput() {
        if (!Keyboard.isRepeatEvent() && !Keyboard.getEventKeyState()) {
            if (lastClickTime <= Minecraft.getSystemTime() - 20)
                container.setSelectingKeyBinding(null);
        }
    }

    /**
     * call after keyTyped(char, int) handle logic
     */
    public void onKeyTyped() {
        if (container.getSelectingKeyBinding() != null)
            lastClickTime = Minecraft.getSystemTime();
    }

    public class CategoryEntry implements IGuiListEntry {
        protected final String text;
        protected final int width;

        public CategoryEntry(String name) {
            text = I18n.format(name);
            width = mc.fontRenderer.getStringWidth(text);
        }

        @Override public void drawEntry(
                int slotIndex,
                int x, int y, int listWidth, int slotHeight,
                int mouseX, int mouseY, boolean isSelected, float partialTicks
        ) {
            assert mc.currentScreen != null;
            mc.fontRenderer.drawString(text, (mc.currentScreen.width - width) / 2, y + slotHeight - 8, 0xFFFFFF);
        }

        @Override public boolean mousePressed(int a, int b, int c, int d, int e, int f) {return false;}

        @Override public void mouseReleased(int a, int b, int c, int d, int e, int f) {}

        @Override public void updatePosition(int slotIndex, int x, int y, float partialTicks) {}
    }

    public class KeyEntry implements IGuiListEntry {
        protected final KeyBinding keyBinding;
        protected final String desc;
        protected final GuiButton btnChangeKeyBinding;
        protected final GuiButton btnReset;

        public KeyEntry(KeyBinding keyBinding) {
            this.keyBinding = keyBinding;
            desc = I18n.format(keyBinding.getKeyDescription());
            btnChangeKeyBinding = new GuiButton(0, 0, 0, 95, 20, keyBinding.getDisplayName());
            btnReset = new GuiButton(1, 0, 0, 50, 20, I18n.format("controls.reset"));
        }

        @Override public void drawEntry(
                int slotIndex,
                int x, int y, int listWidth, int slotHeight,
                int mouseX, int mouseY, boolean isSelected, float partialTicks
        ) {
            mc.fontRenderer.drawString(desc, x + 90 - maxLabelTextWidth, y + (slotHeight - 4) / 2, 0xFFFFFF);
            btnReset.x = x + 210;
            btnReset.y = y;
            btnReset.enabled = !keyBinding.isSetToDefaultValue();
            btnReset.drawButton(mc, mouseX, mouseY, partialTicks);
            btnChangeKeyBinding.x = x + 105;
            btnChangeKeyBinding.y = y;
            boolean codeConflict = false, modifierConfilict = true;
            if (keyBinding.getKeyCode() != 0) {
                for (KeyBinding kb : keyBindings) {
                    if (keyBinding != kb && keyBinding.conflicts(kb)) {
                        codeConflict = true;
                        modifierConfilict &= keyBinding.hasKeyCodeModifierConflict(kb);
                    }
                }
            }
            if (container.getSelectingKeyBinding() == keyBinding) {
                btnChangeKeyBinding.displayString =
                        TextFormatting.WHITE + "> " +
                                TextFormatting.YELLOW + keyBinding.getDisplayName() +
                                TextFormatting.WHITE + " <";
            } else if (codeConflict) {
                btnChangeKeyBinding.displayString =
                        (modifierConfilict ? TextFormatting.GOLD : TextFormatting.RED) + keyBinding.getDisplayName();
            } else {
                btnChangeKeyBinding.displayString = keyBinding.getDisplayName();
            }
            btnChangeKeyBinding.drawButton(mc, mouseX, mouseY, partialTicks);
        }

        @Override public boolean mousePressed(
                int slotIndex,
                int mouseX, int mouseY, int mouseEvent,
                int relativeX, int relativeY
        ) {
            if (btnChangeKeyBinding.mousePressed(mc, mouseX, mouseY)) {
                container.setSelectingKeyBinding(keyBinding);
                return true;
            } else if (btnReset.mousePressed(mc, mouseX, mouseY)) {
                keyBinding.setToDefault();
                container.onKeyBindingChanged();
                return true;
            } else {
                return false;
            }
        }

        @Override public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            btnChangeKeyBinding.mouseReleased(x, y);
            btnReset.mouseReleased(x, y);
        }

        @Override public void updatePosition(int slotIndex, int x, int y, float partialTicks) {}
    }

}
