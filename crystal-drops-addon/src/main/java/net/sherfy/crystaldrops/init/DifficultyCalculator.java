package net.sherfy.crystaldrops.init;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.sherfy.crystaldrops.compat.ModCompatibility;

import java.util.Random;

/**
 * Calculates difficulty_level for entities using exponential decay sampling.
 *
 * A candidate level is drawn uniformly from [0, 100] and accepted with
 * probability P = e^(-level / lambda). Lower levels are far more common;
 * high levels are possible but increasingly rare.
 *
 * ── Calibration (target: 75 / 100 combined with Dangerous Forge) ──────────
 *
 * Dangerous Forge contributes ~45% via health scaling, mob gear, creeper/spider
 * attribute buffs, and skeleton weapon-swapping.
 * Crystal Drops targets ~30% via difficulty distribution and frenzy.
 *
 * When Dangerous Forge is NOT loaded, lambda values are raised so Crystal Drops
 * alone sits around 55-60% (playable solo).
 *
 * Lambda reference:
 *   Smaller lambda → steeper decay → fewer hard mobs (easier)
 *   Larger  lambda → flatter decay → more hard mobs  (harder)
 *
 *               With Dangerous   Without Dangerous
 *   Overworld:       10               14
 *   Nether:          16               22
 *   End:             22               30
 *
 * At lambda=10: level 50 is accepted only e^(-5) ≈ 0.7% of the time.
 * LEGENDARY (81+) mobs become genuinely rare encounters.
 *   End:             38               52
 *
 * Passive mobs (animals) are hard-capped at 30 regardless.
 */
public class DifficultyCalculator {

    private static final Random RNG = new Random();

    // Lambda with Dangerous Forge — very steep, high levels extremely rare
    private static final double OVERWORLD_LAMBDA_COMPAT = 10.0;
    private static final double NETHER_LAMBDA_COMPAT    = 16.0;
    private static final double END_LAMBDA_COMPAT       = 22.0;

    // Lambda without Dangerous Forge — steep but slightly more lenient
    private static final double OVERWORLD_LAMBDA_SOLO   = 14.0;
    private static final double NETHER_LAMBDA_SOLO      = 22.0;
    private static final double END_LAMBDA_SOLO         = 30.0;

    // Overworld distance scaling: every 500 blocks reduces lambda (more hard mobs)
    private static final double DISTANCE_LAMBDA_REDUCTION_PER_500 = 2.0;
    private static final double MIN_OVERWORLD_LAMBDA               = 10.0;

    private static final int MAX_ATTEMPTS = 200;

    /**
     * Rolls a difficulty level for the given entity based on its position,
     * dimension, and which companion mods are loaded.
     * Returns a value in [0, 100] with exponential probability decay.
     */
    public static double roll(LivingEntity entity) {
        // Passive mobs have no difficulty level
        if (isPassive(entity)) return 0;

        ResourceKey<Level> dim = entity.level().dimension();
        boolean withDangerous  = ModCompatibility.DANGEROUS_LOADED;

        double lambda;
        if (dim.equals(Level.NETHER)) {
            lambda = withDangerous ? NETHER_LAMBDA_COMPAT : NETHER_LAMBDA_SOLO;
        } else if (dim.equals(Level.END)) {
            lambda = withDangerous ? END_LAMBDA_COMPAT : END_LAMBDA_SOLO;
        } else {
            double base      = withDangerous ? OVERWORLD_LAMBDA_COMPAT : OVERWORLD_LAMBDA_SOLO;
            double reduction = distanceLambdaReduction(entity);
            lambda           = Math.max(base - reduction, MIN_OVERWORLD_LAMBDA);
        }

        return exponentialSample(lambda);
    }

    // ── Core sampling ──────────────────────────────────────────────────────

    /**
     * Rejection sampling: draw uniform [0,100], accept with P = e^(-x/lambda).
     * Produces a true exponential distribution over [0, 100].
     */
    private static double exponentialSample(double lambda) {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            double candidate  = RNG.nextDouble() * 100.0;
            double acceptance = Math.exp(-candidate / lambda);
            if (RNG.nextDouble() < acceptance) {
                return candidate;
            }
        }
        return RNG.nextDouble() * 10.0; // near-impossible fallback
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private static double distanceLambdaReduction(LivingEntity entity) {
        BlockPos pos  = entity.blockPosition();
        double dist   = Math.sqrt(pos.getX() * (double) pos.getX() + pos.getZ() * (double) pos.getZ());
        return (dist / 500.0) * DISTANCE_LAMBDA_REDUCTION_PER_500;
    }

    private static boolean isPassive(LivingEntity entity) {
        return entity instanceof Animal || entity instanceof AgeableMob;
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
