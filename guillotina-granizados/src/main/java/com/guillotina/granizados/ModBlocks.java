package com.guillotina.granizados;

import com.guillotina.granizados.block.GuilotinaBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GranizadosMod.MOD_ID);

    public static final RegistryObject<Block> GUILLOTINA = BLOCKS.register("guillotina",
            () -> new GuilotinaBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5f)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()));

    // Registra el BlockItem automáticamente
    static {
        ModItems.ITEMS.register("guillotina",
                () -> new BlockItem(GUILLOTINA.get(), new Item.Properties()));
    }
}
