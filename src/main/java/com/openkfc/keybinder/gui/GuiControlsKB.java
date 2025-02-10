package com.openkfc.keybinder.gui;

import com.openkfc.keybinder.gui.util.KeyButtonsInitializer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiControlsKB extends GuiScreen {

    @Override public void initGui() {
        new KeyButtonsInitializer(0, width, height >> 1).applyTo(this, buttonList);
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        for (GuiButton gb : buttonList) {
            if (gb.isMouseOver())
                gb.drawButtonForegroundLayer(mouseX, mouseY);
        }
    }

}
