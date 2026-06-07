package com.guillotina.granizados;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GranizadosMod.MOD_ID)
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
}
