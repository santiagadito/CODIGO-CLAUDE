package net.sherfy.crystaldrops.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sherfy.crystaldrops.CrystalDropsMod;
import net.sherfy.crystaldrops.item.SummonScrollItem;

public class CrystalDropsItems {

    public static final DeferredRegister<Item> REGISTRY =
        DeferredRegister.create(ForgeRegistries.ITEMS, CrystalDropsMod.MODID);

    public static final RegistryObject<Item> SUMMON_SCROLL =
        REGISTRY.register("summon_scroll", SummonScrollItem::new);
}
