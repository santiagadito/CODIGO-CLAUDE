package net.sherfy.crystaldrops.init;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Defines mob-specific tiered loot drops.
 *
 * Drop quantities and quality scale with difficulty_level, rewarding
 * players who defeat harder enemies. XP bonuses are calculated separately.
 */
public class MobLootTable {

    private static final Random RNG = new Random();

    /**
     * Returns a list of ItemStacks to spawn when the entity dies.
     * Returns an empty list if the mob type has no special loot defined.
     */
    public static List<ItemStack> getDrops(LivingEntity entity, double difficulty) {
        List<ItemStack> drops = new ArrayList<>();

        if (entity instanceof Zombie || entity instanceof Husk || entity instanceof Drowned) {
            drops.addAll(zombieDrops(difficulty));
        } else if (entity instanceof Skeleton || entity instanceof Stray || entity instanceof WitherSkeleton) {
            drops.addAll(skeletonDrops(entity, difficulty));
        } else if (entity instanceof Creeper) {
            drops.addAll(creeperDrops(difficulty));
        } else if (entity instanceof Blaze || entity instanceof MagmaCube || entity instanceof Ghast
                || entity instanceof Piglin || entity instanceof PiglinBrute || entity instanceof WitherSkeleton) {
            drops.addAll(netherDrops(entity, difficulty));
        } else if (entity instanceof Enderman || entity instanceof Shulker || entity instanceof EndermiteEntity) {
            drops.addAll(endDrops(difficulty));
        } else {
            drops.addAll(genericDrops(difficulty));
        }

        return drops;
    }

    // ── XP Bonus ───────────────────────────────────────────────────────────

    /**
     * Extra XP orb value granted on top of vanilla XP.
     * Scales aggressively with difficulty so rare mobs feel truly rewarding.
     *
     * Level 0-16  : +0  XP
     * Level 17-32 : +2  XP   (slight bonus)
     * Level 33-48 : +6  XP
     * Level 49-64 : +15 XP
     * Level 65-80 : +35 XP
     * Level 81-100: +80 XP   (legendary — worth hunting)
     */
    public static int bonusXp(double difficulty) {
        if (difficulty >= 81) return 80;
        if (difficulty >= 65) return 35;
        if (difficulty >= 49) return 15;
        if (difficulty >= 33) return 6;
        if (difficulty >= 17) return 2;
        return 0;
    }

    // ── Zombie family ──────────────────────────────────────────────────────

    private static List<ItemStack> zombieDrops(double difficulty) {
        List<ItemStack> drops = new ArrayList<>();

        if (difficulty >= 81) {
            // Legendary: enchanted golden apple + iron block
            drops.add(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1));
            drops.add(new ItemStack(Items.IRON_BLOCK, RNG.nextInt(2) + 1));
        } else if (difficulty >= 65) {
            // Rare: golden apple + 2-4 iron ingots
            drops.add(new ItemStack(Items.GOLDEN_APPLE, 1));
            drops.add(new ItemStack(Items.IRON_INGOT, RNG.nextInt(3) + 2));
        } else if (difficulty >= 49) {
            // Uncommon: golden apple 50% + 1-3 iron ingots
            if (RNG.nextFloat() < 0.5f) drops.add(new ItemStack(Items.GOLDEN_APPLE, 1));
            drops.add(new ItemStack(Items.IRON_INGOT, RNG.nextInt(2) + 1));
        } else if (difficulty >= 33) {
            // Common+: 1-2 gold ingots
            drops.add(new ItemStack(Items.GOLD_INGOT, RNG.nextInt(2) + 1));
        } else if (difficulty >= 17) {
            // Common: 1-2 iron ingots
            drops.add(new ItemStack(Items.IRON_INGOT, RNG.nextInt(2) + 1));
        }
        // Weak (0-16): no bonus drop — vanilla loot only

        return drops;
    }

    // ── Skeleton family ────────────────────────────────────────────────────

    private static List<ItemStack> skeletonDrops(LivingEntity entity, double difficulty) {
        List<ItemStack> drops = new ArrayList<>();

        if (difficulty >= 81) {
            // Legendary: enchanted bow (Power V + Infinity) + 16 tipped arrows
            drops.add(enchantedBow(5));
            drops.add(tippedArrow(Items.TIPPED_ARROW, MobEffects.DAMAGE_BOOST, 2, 16));
        } else if (difficulty >= 65) {
            // Rare: enchanted bow (Power III) + 8 tipped poison arrows
            drops.add(enchantedBow(3));
            drops.add(tippedArrow(Items.TIPPED_ARROW, MobEffects.POISON, 1, 8));
        } else if (difficulty >= 49) {
            // Uncommon: 4-8 tipped slowness arrows + extra bones
            drops.add(tippedArrow(Items.TIPPED_ARROW, MobEffects.MOVEMENT_SLOWDOWN, 0, RNG.nextInt(4) + 4));
            drops.add(new ItemStack(Items.BONE, RNG.nextInt(3) + 2));
        } else if (difficulty >= 33) {
            // Common+: 2-5 extra bones + 50% spectral arrow
            drops.add(new ItemStack(Items.BONE, RNG.nextInt(3) + 2));
            if (RNG.nextFloat() < 0.5f) drops.add(new ItemStack(Items.SPECTRAL_ARROW, RNG.nextInt(3) + 1));
        } else if (difficulty >= 17) {
            // Common: 1-3 extra arrows
            drops.add(new ItemStack(Items.ARROW, RNG.nextInt(3) + 1));
        }

        // Wither skeletons get a coal/wither skull bonus regardless
        if (entity instanceof WitherSkeleton) {
            drops.add(new ItemStack(Items.COAL, RNG.nextInt(3) + 1));
            if (difficulty >= 65 && RNG.nextFloat() < 0.15f) {
                drops.add(new ItemStack(Items.WITHER_SKELETON_SKULL, 1));
            }
        }

        return drops;
    }

    // ── Creeper ────────────────────────────────────────────────────────────

    private static List<ItemStack> creeperDrops(double difficulty) {
        List<ItemStack> drops = new ArrayList<>();

        if (difficulty >= 81) {
            // Legendary: creeper head guaranteed + 2 TNT + 4-8 gunpowder
            drops.add(new ItemStack(Items.CREEPER_HEAD, 1));
            drops.add(new ItemStack(Items.TNT, 2));
            drops.add(new ItemStack(Items.GUNPOWDER, RNG.nextInt(4) + 4));
        } else if (difficulty >= 65) {
            // Rare: 25% creeper head + 1 TNT + 3-6 gunpowder
            if (RNG.nextFloat() < 0.25f) drops.add(new ItemStack(Items.CREEPER_HEAD, 1));
            drops.add(new ItemStack(Items.TNT, 1));
            drops.add(new ItemStack(Items.GUNPOWDER, RNG.nextInt(3) + 3));
        } else if (difficulty >= 49) {
            // Uncommon: 50% TNT + 2-4 gunpowder
            if (RNG.nextFloat() < 0.5f) drops.add(new ItemStack(Items.TNT, 1));
            drops.add(new ItemStack(Items.GUNPOWDER, RNG.nextInt(3) + 2));
        } else if (difficulty >= 33) {
            // Common+: 2-4 gunpowder
            drops.add(new ItemStack(Items.GUNPOWDER, RNG.nextInt(2) + 2));
        } else if (difficulty >= 17) {
            // Common: 1-2 gunpowder
            drops.add(new ItemStack(Items.GUNPOWDER, RNG.nextInt(2) + 1));
        }

        return drops;
    }

    // ── Nether mobs ────────────────────────────────────────────────────────

    private static List<ItemStack> netherDrops(LivingEntity entity, double difficulty) {
        List<ItemStack> drops = new ArrayList<>();

        if (difficulty >= 81) {
            drops.add(new ItemStack(Items.NETHERITE_SCRAP, 1));
            drops.add(new ItemStack(Items.BLAZE_POWDER, RNG.nextInt(4) + 4));
            drops.add(new ItemStack(Items.QUARTZ, RNG.nextInt(8) + 4));
        } else if (difficulty >= 65) {
            drops.add(new ItemStack(Items.MAGMA_CREAM, RNG.nextInt(3) + 2));
            drops.add(new ItemStack(Items.BLAZE_POWDER, RNG.nextInt(3) + 2));
            drops.add(new ItemStack(Items.QUARTZ, RNG.nextInt(4) + 2));
        } else if (difficulty >= 49) {
            drops.add(new ItemStack(Items.BLAZE_POWDER, RNG.nextInt(2) + 1));
            drops.add(new ItemStack(Items.QUARTZ, RNG.nextInt(4) + 2));
            if (RNG.nextFloat() < 0.3f) drops.add(new ItemStack(Items.GOLD_INGOT, RNG.nextInt(2) + 1));
        } else if (difficulty >= 33) {
            drops.add(new ItemStack(Items.QUARTZ, RNG.nextInt(3) + 1));
            if (entity instanceof Blaze) drops.add(new ItemStack(Items.BLAZE_ROD, 1));
        } else if (difficulty >= 17) {
            drops.add(new ItemStack(Items.QUARTZ, RNG.nextInt(2) + 1));
        }

        return drops;
    }

    // ── End mobs ───────────────────────────────────────────────────────────

    private static List<ItemStack> endDrops(double difficulty) {
        List<ItemStack> drops = new ArrayList<>();

        if (difficulty >= 81) {
            // Legendary: dragon breath + chorus fruit + ender pearl stack
            drops.add(new ItemStack(Items.DRAGON_BREATH, RNG.nextInt(2) + 1));
            drops.add(new ItemStack(Items.CHORUS_FRUIT, RNG.nextInt(4) + 4));
            drops.add(new ItemStack(Items.ENDER_PEARL, RNG.nextInt(4) + 4));
        } else if (difficulty >= 65) {
            drops.add(new ItemStack(Items.CHORUS_FRUIT, RNG.nextInt(3) + 2));
            drops.add(new ItemStack(Items.ENDER_PEARL, RNG.nextInt(3) + 2));
            if (RNG.nextFloat() < 0.2f) drops.add(new ItemStack(Items.SHULKER_SHELL, 1));
        } else if (difficulty >= 49) {
            drops.add(new ItemStack(Items.ENDER_PEARL, RNG.nextInt(2) + 1));
            drops.add(new ItemStack(Items.CHORUS_FRUIT, RNG.nextInt(2) + 1));
        } else if (difficulty >= 33) {
            drops.add(new ItemStack(Items.ENDER_PEARL, RNG.nextInt(2) + 1));
        } else if (difficulty >= 17) {
            if (RNG.nextFloat() < 0.4f) drops.add(new ItemStack(Items.ENDER_PEARL, 1));
        }

        return drops;
    }

    // ── Generic fallback ───────────────────────────────────────────────────

    private static List<ItemStack> genericDrops(double difficulty) {
        List<ItemStack> drops = new ArrayList<>();

        if (difficulty >= 81) drops.add(new ItemStack(Items.NETHER_STAR, 1));
        else if (difficulty >= 65) drops.add(new ItemStack(Items.DIAMOND, RNG.nextInt(2) + 1));
        else if (difficulty >= 49) drops.add(new ItemStack(Items.EMERALD, RNG.nextInt(2) + 1));
        else if (difficulty >= 33) drops.add(new ItemStack(Items.IRON_INGOT, RNG.nextInt(3) + 1));
        else if (difficulty >= 17) drops.add(new ItemStack(Items.GOLD_INGOT, RNG.nextInt(2) + 1));

        return drops;
    }

    // ── Item builders ──────────────────────────────────────────────────────

    private static ItemStack enchantedBow(int powerLevel) {
        ItemStack bow = new ItemStack(Items.BOW);
        bow.enchant(Enchantments.POWER_ARROWS, powerLevel);
        if (powerLevel >= 4) bow.enchant(Enchantments.INFINITY_ARROWS, 1);
        bow.enchant(Enchantments.UNBREAKING, Math.min(powerLevel, 3));
        return bow;
    }

    private static ItemStack tippedArrow(Item arrowItem, net.minecraft.world.effect.MobEffect effect,
                                          int amplifier, int count) {
        ItemStack stack = new ItemStack(arrowItem, count);
        PotionUtils.setCustomEffects(stack, List.of(
            new MobEffectInstance(effect, 100, amplifier)
        ));
        return stack;
    }
}
