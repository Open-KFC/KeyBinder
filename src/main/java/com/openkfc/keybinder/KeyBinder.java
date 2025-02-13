package com.openkfc.keybinder;

import com.openkfc.keybinder.gui.GuiControlsKB;
import net.minecraft.client.gui.GuiControls;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.openkfc.keybinder.KeyBinder.*;

@Mod(
        modid = MODID,
        name = NAME,
        version = VERSION,
        clientSideOnly = true
)
public class KeyBinder {

    public static final String MODID = "keybinder", NAME = "KeyBinder", VERSION = "0.1.1";

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(getClass());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui().getClass() == GuiControls.class)
            event.setGui(new GuiControlsKB());
    }

}
