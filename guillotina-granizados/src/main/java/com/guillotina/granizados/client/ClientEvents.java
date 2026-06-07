package com.guillotina.granizados.client;

import com.guillotina.granizados.GranizadosMod;
import com.guillotina.granizados.ModMenuTypes;
import com.guillotina.granizados.screen.GuilotinaScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterMenuScreensEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GranizadosMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.GUILLOTINA_MENU.get(), GuilotinaScreen::new);
    }
}
