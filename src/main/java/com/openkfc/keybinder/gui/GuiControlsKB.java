package com.openkfc.keybinder.gui;

import com.google.common.collect.ImmutableMap;
import com.openkfc.keybinder.gui.util.KeyButtonsInitializer;
import com.openkfc.keybinder.gui.util.OtherKeyButtonsInitializer;
import com.openkfc.keybinder.gui.util.AlphanumericKeyButtonsInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyModifier;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Arrays;

//TODO: revice logics
public class GuiControlsKB extends GuiScreen {
    public enum ContentType {ALPHANUMERIC, OTHER_KEYS}

    protected final ImmutableMap<KeyBinding, ImmutablePair<KeyModifier, Integer>> originalBindingStatus;
    protected GuiScreen parentScreen = Minecraft.getMinecraft().currentScreen;
    protected GameSettings settings = Minecraft.getMinecraft().gameSettings;
    protected String screenTitle = "Controls";
    protected ContentType contentType = ContentType.ALPHANUMERIC;
    protected int keyboardViewTop, keyboardViewButtom, keyboardViewLeft, keyboardViewRight;
    protected String bindingSearchText;
    protected int bindingSearchTextX = 30, bindingSearchTextY = 28;
    protected GuiTextField bindingSearchField;
    protected boolean reInitOnDrawing = false;

    public GuiControlsKB() {
        ImmutableMap.Builder<KeyBinding, ImmutablePair<KeyModifier, Integer>> stateMapBuilder = ImmutableMap.builder();
        for (KeyBinding kb : settings.keyBindings)
            stateMapBuilder.put(kb, ImmutablePair.of(kb.getKeyModifier(), kb.getKeyCode()));
        originalBindingStatus = stateMapBuilder.build();
    }

    @Override public void initGui() {
        KeyButtonsInitializer kbi;
        try {
            switch (contentType) {
                case ALPHANUMERIC:
                    kbi = new AlphanumericKeyButtonsInitializer(0, width, 50, height - 50 - 60);
                    break;
                case OTHER_KEYS:
                    kbi = new OtherKeyButtonsInitializer(0, width, 50, height - 50 - 60);
                    break;
                default:
                    return;
            }
        } catch (IllegalArgumentException e) {
            return;
        }
        kbi.applyTo(this, buttonList);
        keyboardViewTop = kbi.y;
        keyboardViewButtom = kbi.y + kbi.height;
        keyboardViewLeft = kbi.x;
        keyboardViewRight = kbi.x + kbi.width;
        screenTitle = I18n.format("controls.title");
        initButtons();
        initTextField();
    }

    protected void initButtons() {
        //TODO: display reset btn and undo btn as disabled
        //TODO: I18n
        int btnWidth = (width - 70) / 3;
        int[] aX = new int[]{30, 30 + btnWidth + 5, 30 + 2 * (btnWidth + 5)};
        int[] aY = new int[]{height - 29, height - 54};
        addButton(new GuiButton(10000, aX[0], aY[0], I18n.format("controls.resetAll"))).setWidth(btnWidth);
        addButton(new GuiButton(10001, aX[1], aY[0], "Undo")).setWidth(btnWidth);
        addButton(new GuiButton(10003, aX[2], aY[0], I18n.format("gui.done"))).setWidth(btnWidth);
        addButton(new GuiButton(10004, aX[0], aY[1], "Other Settings")).setWidth(btnWidth);
        addButton(new GuiButton(10005, aX[1], aY[1], "Other Keys")).setWidth(btnWidth);
        addButton(new GuiButton(10006, aX[2], aY[1], "Show KeyBindings")).setWidth(btnWidth);
    }

    protected void initTextField() {
        //TODO: I18n
        bindingSearchText = "Search KeyBinding";
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

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (reInitOnDrawing) {
            reInitOnDrawing = false;
            buttonList.clear();
            initGui();
        }
        drawBg();
        drawCenteredString(fontRenderer, screenTitle, width >> 1, 8, 0xFFFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
        fontRenderer.drawStringWithShadow(bindingSearchText, bindingSearchTextX, bindingSearchTextY, 0xFFFFFFFF);
        bindingSearchField.drawTextBox();
        for (GuiButton gb : buttonList) {
            if (gb.isMouseOver())
                gb.drawButtonForegroundLayer(mouseX, mouseY);
        }
    }

    @Override protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        bindingSearchField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override protected void keyTyped(char typedChar, int keyCode) {
        if (bindingSearchField.textboxKeyTyped(typedChar, keyCode)) {
            String text = bindingSearchField.getText().trim();
            for (GuiButton gb : buttonList)
                if (gb instanceof KeyButton)
                    ((KeyButton) gb).flushHighlightWithSearchBoxString(text);
        } else {
            if (keyCode == Keyboard.KEY_ESCAPE)
                quitScreen();
        }
    }

    @Override protected void actionPerformed(@SuppressWarnings("NullableProblems") GuiButton button) {
        if (button instanceof KeyButton) {
            int keyCode = button.id;
            mc.displayGuiScreen(new GuiKeyBindingEdit(
                    this,
                    () -> I18n.format("controls.title") + " > " + GameSettings.getKeyDisplayString(keyCode) + " <",
                    ((KeyButton) button).relatedKeyBindings
            ));
            return;
        }
        switch (button.id) {
            case 10000: //reset all
                for (KeyBinding kb : settings.keyBindings)
                    kb.setToDefault();
                flushKeyBindings();
                break;
            case 10001: //undo
                originalBindingStatus.forEach((kb, ip) -> kb.setKeyModifierAndCode(ip.left, ip.right));
                flushKeyBindings();
                break;
            case 10003: //done
                quitScreen();
                break;
            case 10004: //other settings
                mc.displayGuiScreen(new GuiOtherControlSettings(this));
                break;
            case 10005: //other keys
                ContentType[] values = ContentType.values();
                contentType = values[(contentType.ordinal() + 1) % values.length];
                reInitOnDrawing = true;
                break;
            case 10006: //keybindings screen
                mc.displayGuiScreen(new GuiKeyBindingEdit(
                        this,
                        () -> I18n.format("controls.title"),
                        Arrays.asList(mc.gameSettings.keyBindings)
                ));
                break;
        }
    }

    protected void flushKeyBindings() {
        KeyBinding.resetKeyBindingArrayAndHash();
        for (GuiButton gb : buttonList)
            if (gb instanceof KeyButton)
                ((KeyButton) gb).flushRelatedKeyBindings();
    }

    protected void quitScreen() {
        mc.gameSettings.saveOptions();
        mc.displayGuiScreen(parentScreen);
        if (mc.currentScreen == null)
            mc.setIngameFocus();
    }

    protected void drawBg() {
        drawDefaultBackground();
        drawBgTexLight(0, 0, width, height);
        drawBgTexDark(keyboardViewLeft, keyboardViewTop, keyboardViewRight, keyboardViewButtom);
    }

    public static void drawBgTexDark(double l, double t, double r, double b) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Minecraft.getMinecraft().renderEngine.bindTexture(OPTIONS_BACKGROUND);
        GlStateManager.color(1, 1, 1, 1);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(l, t, 0).tex(l * 0.03125f, t * 0.03125f).color(32, 32, 32, 255).endVertex();
        buffer.pos(l, b, 0).tex(l * 0.03125f, b * 0.03125f).color(32, 32, 32, 255).endVertex();
        buffer.pos(r, b, 0).tex(r * 0.03125f, b * 0.03125f).color(32, 32, 32, 255).endVertex();
        buffer.pos(r, t, 0).tex(r * 0.03125f, t * 0.03125f).color(32, 32, 32, 255).endVertex();
        tessellator.draw();
    }

    public static void drawBgTexLight(double l, double t, double r, double b) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Minecraft.getMinecraft().renderEngine.bindTexture(OPTIONS_BACKGROUND);
        GlStateManager.color(1, 1, 1, 1);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(l, t, 0).tex(l * 0.03125f, t * 0.03125f).color(64, 64, 64, 255).endVertex();
        buffer.pos(l, b, 0).tex(l * 0.03125f, b * 0.03125f).color(64, 64, 64, 255).endVertex();
        buffer.pos(r, b, 0).tex(r * 0.03125f, b * 0.03125f).color(64, 64, 64, 255).endVertex();
        buffer.pos(r, t, 0).tex(r * 0.03125f, t * 0.03125f).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
    }
}
