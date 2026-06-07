package net.sherfy.crystaldrops.init;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * Calculates difficulty_level for entities using a Gaussian distribution
 * biased toward low values. Level scales with world distance and dimension.
 *
 * Passive mobs (animals) are hard-capped at 30.
 */
public class DifficultyCalculator {

    private static final Random RNG = new Random();

    // Baseline Gaussian parameters per dimension
    private static final double OVERWORLD_MEAN   = 12.0;
    private static final double OVERWORLD_STDDEV  = 14.0;
    private static final double NETHER_MEAN      = 42.0;
    private static final double NETHER_STDDEV    = 18.0;
    private static final double END_MEAN         = 62.0;
    private static final double END_STDDEV       = 16.0;

    // Difficulty bonus per 500 blocks from world spawn (overworld only)
    private static final double DISTANCE_BONUS_PER_500 = 5.0;
    private static final double MAX_DISTANCE_BONUS      = 30.0;

    private static final double PASSIVE_MOB_CAP = 30.0;

    /**
     * Rolls a difficulty level for the given entity based on its position and dimension.
     * Returns a value clamped to [0, 100].
     */
    public static double roll(LivingEntity entity) {
        ResourceKey<Level> dim = entity.level().dimension();
        double mean;
        double stddev;

        if (dim.equals(Level.NETHER)) {
            mean   = NETHER_MEAN;
            stddev = NETHER_STDDEV;
        } else if (dim.equals(Level.END)) {
            mean   = END_MEAN;
            stddev = END_STDDEV;
        } else {
            // Overworld: scale mean with horizontal distance from spawn
            double distanceBonus = distanceBonus(entity);
            mean   = OVERWORLD_MEAN + distanceBonus;
            stddev = OVERWORLD_STDDEV;
        }

        // Box-Muller Gaussian sample
        double raw = gaussianSample(mean, stddev);
        double level = clamp(raw, 0, 100);

        // Passive mob cap
        if (isPassive(entity)) {
            level = Math.min(level, PASSIVE_MOB_CAP);
        }

        return level;
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private static double distanceBonus(LivingEntity entity) {
        BlockPos pos = entity.blockPosition();
        double dist = Math.sqrt(pos.getX() * (double) pos.getX() + pos.getZ() * (double) pos.getZ());
        double bonus = (dist / 500.0) * DISTANCE_BONUS_PER_500;
        return Math.min(bonus, MAX_DISTANCE_BONUS);
    }

    private static boolean isPassive(LivingEntity entity) {
        return entity instanceof Animal || entity instanceof AgeableMob;
    }

    private static double gaussianSample(double mean, double stddev) {
        return mean + RNG.nextGaussian() * stddev;
    }

    private static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    /**
     * Returns a human-readable tier name for logging/display.
     */
    public static String tierName(double difficulty) {
        if (difficulty >= 81) return "LEGENDARY";
        if (difficulty >= 65) return "RARE";
        if (difficulty >= 49) return "UNCOMMON";
        if (difficulty >= 33) return "COMMON+";
        if (difficulty >= 17) return "COMMON";
        return "WEAK";
    }
}
