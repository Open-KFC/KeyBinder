package com.openkfc.keybinder;

import com.openkfc.keybinder.gui.GuiControlsKB;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import static com.openkfc.keybinder.KeyBinder.*;

@Mod(
        modid = MODID,
        name = NAME,
        version = VERSION,
        clientSideOnly = true
)
public class KeyBinder {

    public static final String MODID = "keybinder", NAME = "KeyBinder", VERSION = "0.1.0";

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(getClass());
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Minecraft.getMinecraft().gameSettings.keyBindSwapHands.isKeyDown()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiControlsKB());
        }
    }

}
