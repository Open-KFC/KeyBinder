package com.openkfc.keybinder.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class GuiOtherControlSettings extends GuiScreen {
    public static final List<GameSettings.Options> staticOptions = Lists.newArrayList(
            GameSettings.Options.INVERT_MOUSE, GameSettings.Options.SENSITIVITY,
            GameSettings.Options.TOUCHSCREEN, GameSettings.Options.AUTO_JUMP
    );
    protected final GameSettings settings = Minecraft.getMinecraft().gameSettings;
    protected GuiScreen parentScreen;
    protected String screenTitle = "Controls";

    public GuiOtherControlSettings(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override public void initGui() {
        screenTitle = I18n.format("controls.title");
        addButton(new GuiButton(-10000, width / 2 - 100, height - 29, I18n.format("gui.done")));
        int counter = 0;
        for (GameSettings.Options option : staticOptions) {
            if (option.isFloat())
                addButton(new GuiOptionSlider(
                        option.getOrdinal(),
                        this.width / 2 - 155 + counter % 2 * 160, 28 + 24 * (counter >> 1),
                        option
                ));
            else
                addButton(new GuiOptionButton(
                        option.getOrdinal(),
                        this.width / 2 - 155 + counter % 2 * 160, 28 + 24 * (counter >> 1),
                        option, settings.getKeyBinding(option)
                ));
            counter++;
        }
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GuiControlsKB.drawBgTexLight(0, 0, width, height);
        GuiControlsKB.drawBgTexDark(0, 24, width, height - 40);
        drawCenteredString(mc.fontRenderer, screenTitle, width / 2, 8, 0xFFFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE)
            quitScreen();
    }

    @Override protected void actionPerformed(GuiButton button) {
        if (button.id == -10000) {
            quitScreen();
            return;
        }
        if (button instanceof GuiOptionButton) {
            settings.setOptionValue(((GuiOptionButton) button).getOption(), 1);
            button.displayString = settings.getKeyBinding(GameSettings.Options.byOrdinal(button.id));
        }
    }

    protected void quitScreen() {
        mc.displayGuiScreen(parentScreen);
        if (mc.currentScreen == null)
            mc.setIngameFocus();
    }
}
