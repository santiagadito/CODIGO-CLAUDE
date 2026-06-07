package net.sherfy.crystaldrops;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sherfy.crystaldrops.init.CrystalDropsItems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CrystalDropsMod.MODID)
public class CrystalDropsMod {

    public static final String MODID = "crystaldrops";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public CrystalDropsMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        CrystalDropsItems.REGISTRY.register(modBus);
        LOGGER.info("Crystal Drops Addon initialized.");
    }
}
