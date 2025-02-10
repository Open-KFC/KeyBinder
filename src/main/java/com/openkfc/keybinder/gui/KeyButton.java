package com.openkfc.keybinder.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class KeyButton extends GuiButton {
    protected final GuiScreen screen;
    protected final int keyCode;
    public final List<KeyBinding> relatedKeyBindings = new ArrayList<>();
    protected final List<String> keyBindingsNames = new ArrayList<>();

    public KeyButton(
            GuiScreen screen, int keyCode, int x, int y, int size, String buttonText
    ) {
        this(screen, keyCode, x, y, size, size, buttonText);
    }

    public KeyButton(
            GuiScreen screen, int keyCode, int x, int y, int widthIn, int heightIn, String buttonText
    ) {
        super(keyCode, x, y, widthIn, heightIn, buttonText);
        this.screen = screen;
        this.keyCode = keyCode;
        flushRelatedKeyBindings();
    }

    @Override public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!visible)
            return;
        FontRenderer fr = mc.fontRenderer;
        int hs = getHoverState(hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height);
        if (relatedKeyBindings.isEmpty()) {
            drawRect(x, y, x + width, y + height, 0xFFAFAFAF);
        } else {
            drawRect(x, y, x + width, y + height, 0xFF3C10FF);
        }
        drawCenteredString(fr, displayString, x + (width >> 1), y + (height >> 1) - 4, 0xFFFFFFFF);
    }

    @Override public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return super.mousePressed(mc, mouseX, mouseY);
    }

    @Override public void drawButtonForegroundLayer(int mouseX, int mouseY) {
        screen.drawHoveringText(keyBindingsNames, mouseX, mouseY);
    }

    protected void flushRelatedKeyBindings() {
        relatedKeyBindings.clear();
        keyBindingsNames.clear();
        findRelatedKeyBindings(keyCode, relatedKeyBindings);
        for (KeyBinding kb : relatedKeyBindings)
            keyBindingsNames.add(I18n.format(kb.getKeyCategory()) + ':' + I18n.format(kb.getKeyDescription()));
        if (!keyBindingsNames.isEmpty())
            keyBindingsNames.add(0, TextFormatting.GOLD + "Related KeyBindings");
    }

    protected static void findRelatedKeyBindings(int keyCode, Collection<KeyBinding> out) {
        for (KeyBinding kb : Minecraft.getMinecraft().gameSettings.keyBindings)
            if (kb.getKeyCode() == keyCode)
                out.add(kb);
    }

    protected static void drawBtnBgXYWH(double x, double y, double w, double h, int color) {
        drawBtnBgLTRB(x, y, x + w, y + h, color);
    }

    protected static void drawBtnBgLTRB(double l, double t, double r, double b, int color) {
        int colorR = (color >>> 16) & 0xFF;
        int colorG = (color >>> 8) & 0xFF;
        int colorB = color & 0xFF;
        GlStateManager.disableTexture2D();
        drawRectLTRB(l + .5, t, r - .5, t + 1, colorR, colorG, colorB);
        drawRectLTRB(l + .5, b - 1, r - .5, b, colorR, colorG, colorB);
        drawRectLTRB(l, t + .5, l + 1, b - .5, colorR, colorG, colorB);
        drawRectLTRB(r - 1, t + .5, r, b - .5, colorR, colorG, colorB);
        GlStateManager.enableTexture2D();
    }

    protected static void drawRectLTRB(double l, double t, double r, double b, int colorR, int colorG, int colorB) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(l, t, 0).color(colorR, colorG, colorB, 0xFF).endVertex();
        buffer.pos(l, b, 0).color(colorR, colorG, colorB, 0xFF).endVertex();
        buffer.pos(r, b, 0).color(colorR, colorG, colorB, 0xFF).endVertex();
        buffer.pos(r, t, 0).color(colorR, colorG, colorB, 0xFF).endVertex();
        tessellator.draw();
    }

}
