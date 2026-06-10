package net.sherfy.crystaldrops.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sherfy.crystaldrops.CrystalDropsMod;
import net.sherfy.crystaldrops.block.SpecialAnvilBlock;

/**
 * Registers the Special Anvil ("Forja del Vacío") block and its item form.
 */
public class CrystalDropsBlocks {

    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, CrystalDropsMod.MODID);

    public static final DeferredRegister<Item> BLOCK_ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, CrystalDropsMod.MODID);

    public static final RegistryObject<Block> SPECIAL_ANVIL =
        BLOCKS.register("special_anvil", () ->
            new SpecialAnvilBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL)));

    public static final RegistryObject<Item> SPECIAL_ANVIL_ITEM =
        BLOCK_ITEMS.register("special_anvil", () ->
            new BlockItem(SPECIAL_ANVIL.get(), new Item.Properties().fireResistant()));
}
