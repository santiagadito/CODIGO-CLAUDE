package net.sherfy.crystaldrops.nirvana;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Decoupled references to the Nirvana mod (modid "nirvana").
 *
 * We never compile against Nirvana's classes — everything is resolved at
 * runtime through ForgeRegistries by ResourceLocation, so this addon builds
 * and loads fine whether or not Nirvana is installed.
 */
public final class NirvanaRefs {

    public static final String MODID = "nirvana";

    public static final ResourceLocation HEMP_SEEDS = id("hemp_seeds");
    public static final ResourceLocation JOINT       = id("joint");
    public static final ResourceLocation BONG        = id("bong");
    public static final ResourceLocation POTION_BONG = id("potion_bong");
    public static final ResourceLocation OLD_PIPE    = id("old_pipe");
    public static final ResourceLocation HEMP_BLOCK  = id("hemp");
    public static final ResourceLocation WILD_HEMP   = id("wild_hemp");
    public static final ResourceLocation PEACE       = id("peace");

    private NirvanaRefs() {}

    private static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static boolean isItem(ItemStack stack, ResourceLocation id) {
        if (stack.isEmpty()) return false;
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return id.equals(key);
    }

    /** True if the stack is any smokeable Nirvana item (joint, bong, pipe). */
    public static boolean isSmokeItem(ItemStack stack) {
        return isItem(stack, JOINT)
            || isItem(stack, BONG)
            || isItem(stack, POTION_BONG)
            || isItem(stack, OLD_PIPE);
    }

    public static boolean isBong(ItemStack stack) {
        return isItem(stack, BONG) || isItem(stack, POTION_BONG);
    }

    public static boolean isHempBlock(Block block) {
        ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
        return HEMP_BLOCK.equals(key) || WILD_HEMP.equals(key);
    }

    public static boolean isPeaceEffect(MobEffect effect) {
        ResourceLocation key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        return PEACE.equals(key);
    }

    public static Item item(ResourceLocation id) {
        return ForgeRegistries.ITEMS.getValue(id);
    }

    /** A stack of a Nirvana item by id, or empty if Nirvana isn't installed. */
    public static ItemStack stack(ResourceLocation id, int count) {
        Item item = ForgeRegistries.ITEMS.getValue(id);
        return item == null ? ItemStack.EMPTY : new ItemStack(item, count);
    }
}
