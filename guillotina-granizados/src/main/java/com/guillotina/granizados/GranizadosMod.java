package com.guillotina.granizados;

import com.guillotina.granizados.command.GranizadosCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GranizadosMod.MOD_ID)
@Mod.EventBusSubscriber(modid = GranizadosMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GranizadosMod {
    public static final String MOD_ID = "guillotina_granizados";

    public GranizadosMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModMenuTypes.MENUS.register(bus);
        ModCreativeModeTabs.TABS.register(bus);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        GranizadosCommand.register(event.getDispatcher());
    }
}
