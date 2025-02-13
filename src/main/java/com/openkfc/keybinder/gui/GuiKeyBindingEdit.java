package com.openkfc.keybinder.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

/**
 * GuiScreen with title + search box + key binding edit list + 'done' button
 */
public class GuiKeyBindingEdit extends GuiScreen /*implements MyKeyBindingList.IKeyBindingListContainer*/ {
    protected GuiScreen parentScreen;
    protected String screenTitle = "Controls";
    protected Supplier<String> titleStrSupplier;
    protected List<KeyBinding> contents;
    protected MyKeyBindingList keyBindingList;
    protected String bindingSearchText;
    protected int bindingSearchTextX = 30, bindingSearchTextY = 28;
    protected GuiTextField bindingSearchField;

    public GuiKeyBindingEdit(GuiScreen parentScreen, Supplier<String> titleStrSupplier, List<KeyBinding> contents) {
        this.parentScreen = parentScreen;
        this.titleStrSupplier = titleStrSupplier;
        this.contents = contents;
    }

    @Override public void initGui() {
        screenTitle = titleStrSupplier.get();
        keyBindingList = new MyKeyBindingList(mc, contents, width + 45, height, 50, height - 32);
        addButton(new GuiButton(-10000, width / 2 - 100, height - 29, I18n.format("gui.done")));
        bindingSearchText = I18n.format("controlskb.search_keybinding");
        int fieldX = bindingSearchTextX + fontRenderer.getStringWidth(bindingSearchText) + 10;
        int fieldW = width - 30 - fieldX;
        String lastFieldText;
        if (bindingSearchField != null)
            lastFieldText = bindingSearchField.getText().trim();
        else
            lastFieldText = null;
        bindingSearchField = new GuiTextField(10100, fontRenderer, fieldX, 24, fieldW, 16);
        bindingSearchField.setTextColor(-1);
        bindingSearchField.setDisabledTextColour(-1);
        if (lastFieldText != null) {
            bindingSearchField.setText(lastFieldText);
            for (GuiButton gb : buttonList)
                if (gb instanceof KeyButton)
                    ((KeyButton) gb).flushHighlightWithSearchBoxString(lastFieldText);
        }
    }

    @Override public void handleKeyboardInput() throws IOException {
        keyBindingList.handleKeyboardInput();
        super.handleKeyboardInput();
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
        if (!keyBindingList.mouseClicked(mouseX, mouseY, mouseButton)) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            bindingSearchField.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state != 0 || !keyBindingList.mouseReleased(mouseX, mouseY, state))
            super.mouseReleased(mouseX, mouseY, state);
    }

    @Override protected void keyTyped(char typedChar, int keyCode) {
        if (bindingSearchField.textboxKeyTyped(typedChar, keyCode)) {
            String text = bindingSearchField.getText().trim();
            List<KeyBinding> listContent;
            if (text.isEmpty()) {
                listContent = contents;
            } else {
                listContent = new ArrayList<>();
                for (KeyBinding kb : contents)
                    if (containsIgnoreCase(I18n.format(kb.getKeyDescription()), text)
                            || containsIgnoreCase(I18n.format(kb.getKeyCategory()), text))
                        listContent.add(kb);
            }
            keyBindingList.initEntries(listContent);
        } else if (!keyBindingList.keyTyped(typedChar, keyCode)) {
            if (keyCode == Keyboard.KEY_ESCAPE)
                quitScreen();
        }
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRenderer, screenTitle, width / 2, 8, 0xFFFFFFFF);
        fontRenderer.drawStringWithShadow(bindingSearchText, bindingSearchTextX, bindingSearchTextY, 0xFFFFFFFF);
        bindingSearchField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void quitScreen() {
        mc.gameSettings.saveOptions();
        mc.displayGuiScreen(parentScreen);
        if (mc.currentScreen == null)
            mc.setIngameFocus();
    }
}
