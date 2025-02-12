package com.openkfc.keybinder.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

/**
 * GuiScreen with only title + key binding edit list + 'done' button
 */
public class GuiKeyBindingEdit extends GuiScreen implements MyKeyBindingList.IKeyBindingListContainer {
    protected GuiScreen parentScreen;
    protected String screenTitle = "Controls";
    protected Supplier<String> titleStrSupplier;
    protected List<KeyBinding> contents;
    protected MyKeyBindingList keyBindingList;
    protected KeyBinding selectingKeyBinding;

    public GuiKeyBindingEdit(GuiScreen parentScreen, Supplier<String> titleStrSupplier, List<KeyBinding> contents) {
        this.parentScreen = parentScreen;
        this.titleStrSupplier = titleStrSupplier;
        this.contents = contents;
    }

    @Override public void initGui() {
        screenTitle = titleStrSupplier.get();
        keyBindingList = new MyKeyBindingList(this, mc, contents, width + 45, height, 24, height - 32);
        addButton(new GuiButton(-10000, width / 2 - 100, height - 29, I18n.format("gui.done")));
    }

    @Override public KeyBinding getSelectingKeyBinding() {return selectingKeyBinding;}

    @Override public void setSelectingKeyBinding(KeyBinding kb) {selectingKeyBinding = kb;}

    @Override public void onKeyBindingChanged() {
        KeyBinding.resetKeyBindingArrayAndHash();
    }

    @Override public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        keyBindingList.handleMouseInput();
    }

    @Override protected void actionPerformed(GuiButton button) {
        if (button.id == -10000)
            quitScreen();
    }

    @Override protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (selectingKeyBinding != null) {
            selectingKeyBinding.setKeyModifierAndCode(KeyModifier.getActiveModifier(), mouseButton - 100);
            onKeyBindingChanged();
            selectingKeyBinding = null;
        } else if (mouseButton != 0 || !keyBindingList.mouseClicked(mouseX, mouseY, mouseButton)) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state != 0 || !keyBindingList.mouseClicked(mouseX, mouseY, state))
            super.mouseReleased(mouseX, mouseY, state);
    }

    @Override protected void keyTyped(char typedChar, int keyCode) {
        if (selectingKeyBinding != null) {
            if (keyCode == Keyboard.KEY_ESCAPE)
                selectingKeyBinding.setKeyModifierAndCode(KeyModifier.NONE, 0);
            else if (keyCode != 0)
                selectingKeyBinding.setKeyModifierAndCode(KeyModifier.getActiveModifier(), keyCode);
            else if (typedChar > 0)
                selectingKeyBinding.setKeyModifierAndCode(KeyModifier.getActiveModifier(), typedChar + 256);
            if (!KeyModifier.isKeyCodeModifier(keyCode))
                selectingKeyBinding = null;
            onKeyBindingChanged();
        } else {
            if (keyCode == Keyboard.KEY_ESCAPE)
                quitScreen();
        }
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRenderer, screenTitle, width / 2, 8, 0xFFFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void quitScreen() {
        mc.gameSettings.saveOptions();
        mc.displayGuiScreen(parentScreen);
        if (mc.currentScreen == null)
            mc.setIngameFocus();
    }
}
