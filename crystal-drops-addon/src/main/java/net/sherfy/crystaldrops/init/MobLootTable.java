package net.sherfy.crystaldrops.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.sherfy.crystaldrops.init.CrystalDropsItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Defines mob-specific tiered loot drops.
 *
 * Skeletons and Creepers also receive the zombie drop table on top of
 * their own drops (shared base loot).
 */
public class MobLootTable {

    private static final Random RNG = new Random();

    public static List<ItemStack> getDrops(LivingEntity entity, double difficulty) {
        List<ItemStack> drops = new ArrayList<>();

        if (entity instanceof Zombie || entity instanceof Husk || entity instanceof Drowned) {
            drops.addAll(zombieDrops(difficulty));
        } else if (entity instanceof Skeleton || entity instanceof Stray || entity instanceof WitherSkeleton) {
            drops.addAll(skeletonDrops(entity, difficulty));
            drops.addAll(zombieDrops(difficulty)); // shared base loot
        } else if (entity instanceof Creeper) {
            drops.addAll(creeperDrops(difficulty));
            drops.addAll(zombieDrops(difficulty)); // shared base loot
        } else if (entity instanceof Blaze || entity instanceof MagmaCube || entity instanceof Ghast
                || entity instanceof Piglin || entity instanceof PiglinBrute) {
            drops.addAll(netherDrops(entity, difficulty));
        } else if (entity instanceof Enderman || entity instanceof Shulker || entity instanceof EndermiteEntity) {
            drops.addAll(endDrops(difficulty));
            drops.addAll(zombieDrops(difficulty)); // shared base loot
        } else {
            drops.addAll(genericDrops(difficulty));
        }

        return drops;
    }

    // ── XP Bonus ───────────────────────────────────────────────────────────

    public static int bonusXp(double difficulty) {
        if (difficulty >= 81) return 80;
        if (difficulty >= 65) return 35;
        if (difficulty >= 49) return 15;
        if (difficulty >= 33) return 6;
        if (difficulty >= 17) return 2;
        return 0;
    }

    // ── Zombie family ──────────────────────────────────────────────────────
    //
    // COMMON    (17-32): 2-6 iron ingots  OR  5-10 coal
    // COMMON+   (33-48): 3 emeralds  OR  5 golden carrots
    // UNCOMMON  (49-64): golden apple  OR  3-10 diamonds
    // RARE      (65-80): enchanted golden apple  OR  1-2 netherite scraps
    // LEGENDARY (81-100): 3 enchanted golden apples + netherite ingot
    //                     + 40% nether star + all lower tiers (maintaining probs)

    private static List<ItemStack> zombieDrops(double difficulty) {
        List<ItemStack> drops = new ArrayList<>();

        if (difficulty >= 81) {
            // Guaranteed legendary core
            drops.add(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 3));
            drops.add(new ItemStack(Items.NETHERITE_INGOT, 1));
            if (RNG.nextFloat() < 0.40f) drops.add(new ItemStack(Items.NETHER_STAR, 1));

            // All lower tiers also roll (maintaining original probabilities)
            // RARE tier
            if (RNG.nextBoolean()) drops.add(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1));
            else drops.add(new ItemStack(Items.NETHERITE_SCRAP, RNG.nextInt(2) + 1));
            // UNCOMMON tier
            if (RNG.nextBoolean()) drops.add(new ItemStack(Items.GOLDEN_APPLE, 1));
            else drops.add(new ItemStack(Items.DIAMOND, RNG.nextInt(8) + 3));
            // COMMON+ tier
            if (RNG.nextBoolean()) drops.add(new ItemStack(Items.EMERALD, 3));
            else drops.add(new ItemStack(Items.GOLDEN_CARROT, 5));
            // COMMON tier
            if (RNG.nextBoolean()) drops.add(new ItemStack(Items.IRON_INGOT, RNG.nextInt(5) + 2));
            else drops.add(new ItemStack(Items.COAL, RNG.nextInt(6) + 5));

        } else if (difficulty >= 65) {
            // RARE: enchanted golden apple OR 1-2 netherite scraps
            if (RNG.nextBoolean()) drops.add(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1));
            else drops.add(new ItemStack(Items.NETHERITE_SCRAP, RNG.nextInt(2) + 1));

        } else if (difficulty >= 49) {
            // UNCOMMON: golden apple OR 3-10 diamonds
            if (RNG.nextBoolean()) drops.add(new ItemStack(Items.GOLDEN_APPLE, 1));
            else drops.add(new ItemStack(Items.DIAMOND, RNG.nextInt(8) + 3));

        } else if (difficulty >= 33) {
            // COMMON+: 3 emeralds OR 5 golden carrots
            if (RNG.nextBoolean()) drops.add(new ItemStack(Items.EMERALD, 3));
            else drops.add(new ItemStack(Items.GOLDEN_CARROT, 5));

        } else if (difficulty >= 17) {
            // COMMON: 2-6 iron ingots OR 5-10 coal
            if (RNG.nextBoolean()) drops.add(new ItemStack(Items.IRON_INGOT, RNG.nextInt(5) + 2));
            else drops.add(new ItemStack(Items.COAL, RNG.nextInt(6) + 5));
        }

        return drops;
    }

    // ── Skeleton family ────────────────────────────────────────────────────
    //
    // COMMON    : 1-3 extra arrows
    // COMMON+   : bones + 50% spectral arrow
    // UNCOMMON  : slowness arrows x4-8
    // RARE      : Bow Power III + poison arrows
    // LEGENDARY : Bow Power V + Infinity + damage arrows
    // Wither bonus: coal + 70% skull at level 65+
    // (Also receives zombie drops on top)

    private static List<ItemStack> skeletonDrops(LivingEntity entity, double difficulty) {
        List<ItemStack> drops = new ArrayList<>();

        if (difficulty >= 81) {
            drops.add(enchantedBow(5));
            drops.add(tippedArrow(MobEffects.DAMAGE_BOOST, 2, RNG.nextInt(8) + 8));
        } else if (difficulty >= 65) {
            drops.add(enchantedBow(3));
            drops.add(tippedArrow(MobEffects.POISON, 1, RNG.nextInt(4) + 4));
        } else if (difficulty >= 49) {
            drops.add(tippedArrow(MobEffects.MOVEMENT_SLOWDOWN, 0, RNG.nextInt(4) + 4));
        } else if (difficulty >= 33) {
            drops.add(new ItemStack(Items.BONE, RNG.nextInt(3) + 2));
            if (RNG.nextFloat() < 0.5f) drops.add(new ItemStack(Items.SPECTRAL_ARROW, RNG.nextInt(3) + 1));
        } else if (difficulty >= 17) {
            drops.add(new ItemStack(Items.ARROW, RNG.nextInt(3) + 1));
        }

        // Wither Skeleton bonus
        if (entity instanceof WitherSkeleton) {
            drops.add(new ItemStack(Items.COAL, RNG.nextInt(3) + 1));
            if (difficulty >= 65 && RNG.nextFloat() < 0.70f) {
                drops.add(new ItemStack(Items.WITHER_SKELETON_SKULL, 1));
            }
        }

        return drops;
    }

    // ── Creeper ────────────────────────────────────────────────────────────
    //
    // COMMON    : 1-5 gunpowder
    // COMMON+   : 6-10 gunpowder
    // UNCOMMON  : 50% TNT + 10-15 gunpowder
    // RARE      : 2 TNT + 15-20 gunpowder
    // LEGENDARY : head guaranteed + 10 TNT + 64 gunpowder
    // (Also receives zombie drops on top)

    private static List<ItemStack> creeperDrops(double difficulty) {
        List<ItemStack> drops = new ArrayList<>();

        if (difficulty >= 81) {
            drops.add(new ItemStack(Items.CREEPER_HEAD, 1));
            drops.add(new ItemStack(Items.TNT, 10));
            drops.add(new ItemStack(Items.GUNPOWDER, 64));
        } else if (difficulty >= 65) {
            drops.add(new ItemStack(Items.TNT, 2));
            drops.add(new ItemStack(Items.GUNPOWDER, RNG.nextInt(6) + 15));
        } else if (difficulty >= 49) {
            if (RNG.nextFloat() < 0.5f) drops.add(new ItemStack(Items.TNT, 1));
            drops.add(new ItemStack(Items.GUNPOWDER, RNG.nextInt(6) + 10));
        } else if (difficulty >= 33) {
            drops.add(new ItemStack(Items.GUNPOWDER, RNG.nextInt(5) + 6));
        } else if (difficulty >= 17) {
            drops.add(new ItemStack(Items.GUNPOWDER, RNG.nextInt(5) + 1));
        }

        return drops;
    }

    // ── Nether mobs ────────────────────────────────────────────────────────
    //
    // COMMON    (17-32): 4-6 gold ingots + 0.5% nether star
    // COMMON+   (33-48): 7-15 gold ingots + 1% nether star
    // UNCOMMON  (49-64): 2 golden apples + 50% enchanted golden apple + 3% nether star
    // RARE      (65-80): 2 enchanted golden apples + netherite ingot + 30% nether star
    // LEGENDARY (81-100): nether star + 4 netherite ingots + 5 enchanted golden apples

    private static List<ItemStack> netherDrops(LivingEntity entity, double difficulty) {
        List<ItemStack> drops = new ArrayList<>();

        if (difficulty >= 81) {
            drops.add(new ItemStack(Items.NETHER_STAR, 1));
            drops.add(new ItemStack(Items.NETHERITE_INGOT, 4));
            drops.add(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 5));
        } else if (difficulty >= 65) {
            drops.add(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 2));
            drops.add(new ItemStack(Items.NETHERITE_INGOT, 1));
            if (RNG.nextFloat() < 0.30f) drops.add(new ItemStack(Items.NETHER_STAR, 1));
        } else if (difficulty >= 49) {
            drops.add(new ItemStack(Items.GOLDEN_APPLE, 2));
            if (RNG.nextFloat() < 0.50f) drops.add(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1));
            if (RNG.nextFloat() < 0.03f) drops.add(new ItemStack(Items.NETHER_STAR, 1));
        } else if (difficulty >= 33) {
            drops.add(new ItemStack(Items.GOLD_INGOT, RNG.nextInt(9) + 7));
            if (RNG.nextFloat() < 0.01f) drops.add(new ItemStack(Items.NETHER_STAR, 1));
        } else if (difficulty >= 17) {
            drops.add(new ItemStack(Items.GOLD_INGOT, RNG.nextInt(3) + 4));
            if (RNG.nextFloat() < 0.005f) drops.add(new ItemStack(Items.NETHER_STAR, 1));
        }

        return drops;
    }

    // ── End mobs ───────────────────────────────────────────────────────────

    private static List<ItemStack> endDrops(double difficulty) {
        List<ItemStack> drops = new ArrayList<>();

        if (difficulty >= 81) {
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

    // ── Summon Scroll drop ────────────────────────────────────────────────

    public static List<ItemStack> getSummonScrollDrops(double difficulty) {
        List<ItemStack> drops = new ArrayList<>();
        if (difficulty < 60) return drops;

        double chance = 0.15 + 0.20 * ((difficulty - 60.0) / 40.0);
        if (RNG.nextDouble() >= chance) return drops;

        int qty = 1 + (int) Math.round(2.0 * ((difficulty - 60.0) / 40.0));
        drops.add(new ItemStack(CrystalDropsItems.SUMMON_SCROLL.get(), qty));
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

    private static ItemStack tippedArrow(net.minecraft.world.effect.MobEffect effect,
                                          int amplifier, int count) {
        ItemStack stack = new ItemStack(Items.TIPPED_ARROW, count);
        PotionUtils.setCustomEffects(stack, List.of(
            new MobEffectInstance(effect, 100, amplifier)
        ));
        return stack;
    }
}
