package com.openkfc.keybinder.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.opengl.GL11;

import java.util.*;

@SuppressWarnings("NullableProblems")
public class KeyButton extends GuiButton {
    protected final GuiScreen screen;
    protected final int keyCode;
    public final List<KeyBinding> relatedKeyBindings = new ArrayList<>();
    protected final List<String> keyBindingsNames = new ArrayList<>();
    protected int backgroundColor = 0xFFAFAFAF;
    protected boolean highlight = false;

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
        hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        drawRect(x, y, x + width, y + height, highlight ? 0xFFDFDF20 : backgroundColor);
        {
            float scale = (width - 1) / (float) fr.getStringWidth(displayString);
            if (scale < 1) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + (width >> 1), y + (height >> 1), 0);
                GlStateManager.scale(scale, scale, 1);
                drawCenteredString(fr, displayString, 0, -4, 0xFFFFFFFF);
                GlStateManager.popMatrix();
            } else {
                drawCenteredString(fr, displayString, x + (width >> 1), y + (height >> 1) - 4, 0xFFFFFFFF);
            }
        }
    }

    @Override public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return super.mousePressed(mc, mouseX, mouseY);
    }

    @Override public void drawButtonForegroundLayer(int mouseX, int mouseY) {
        screen.drawHoveringText(keyBindingsNames, mouseX, mouseY);
    }

    public void flushRelatedKeyBindings() {
        relatedKeyBindings.clear();
        keyBindingsNames.clear();
        findRelatedKeyBindings(keyCode, relatedKeyBindings);
        for (KeyBinding kb : relatedKeyBindings)
            keyBindingsNames.add(displayNameOf(kb));
        if (!keyBindingsNames.isEmpty()) {
            keyBindingsNames.add(0, TextFormatting.GOLD + "Related KeyBindings"); //TODO: I18n
            backgroundColor = hasConflictBinding(relatedKeyBindings) ? 0xFFFF3C10 : 0xFF3C10FF;
        } else {
            backgroundColor = 0xFFAFAFAF;
        }
    }

    public void flushHighlightWithSearchBoxString(String searchBoxStr) { //TODO: revice logic and implementation
        if (searchBoxStr.isEmpty()) {
            highlight = false;
            return;
        }
        for (String kbn : keyBindingsNames) {
            if (kbn.toLowerCase().contains(searchBoxStr.toLowerCase())) {
                highlight = true;
                return;
            }
        }
        highlight = false;
    }

    protected static String displayNameOf(KeyBinding kb) {
        StringBuilder ret = new StringBuilder();
        if (kb.getKeyModifier() != KeyModifier.NONE)
            ret.append("+ ").append(kb.getKeyModifier()).append(" - ");
        ret.append(I18n.format(kb.getKeyCategory())).append(':').append(I18n.format(kb.getKeyDescription()));
        if (kb.getKeyConflictContext() != KeyConflictContext.UNIVERSAL)
            ret.append(" (").append(kb.getKeyConflictContext()).append(')');
        return ret.toString();
    }

    protected static boolean hasConflictBinding(Collection<? extends KeyBinding> keyBindings) {
        if (keyBindings.size() <= 1)
            return false;
        KeyBinding[] processed = new KeyBinding[keyBindings.size()];
        int processedCnt = 0;
        for (KeyBinding kb : keyBindings) {
            for (int i = 0; i < processedCnt; i++) {
                if (kb.conflicts(processed[i]))
                    return true;
            }
            processed[processedCnt++] = kb;
        }
        return false;
    }

    public static void findRelatedKeyBindings(int keyCode, Collection<? super KeyBinding> out) {
        for (KeyBinding kb : Minecraft.getMinecraft().gameSettings.keyBindings)
            if (kb.getKeyCode() == keyCode)
                out.add(kb);
    }

    //TODO: draw custom bg
    @SuppressWarnings("unused")
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
