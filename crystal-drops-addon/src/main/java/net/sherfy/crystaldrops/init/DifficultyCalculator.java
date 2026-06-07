package net.sherfy.crystaldrops.init;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * Calculates difficulty_level for entities using exponential decay sampling.
 *
 * A candidate level is drawn uniformly from [0, 100] and accepted with
 * probability P = e^(-level / lambda). Lower levels are accepted far more
 * often; high levels are possible but increasingly rare.
 *
 * Lambda values per dimension shift the "steepness" of the curve:
 *   Overworld : lambda = 20  → level 60 is ~5% as likely as level 0
 *   Nether    : lambda = 35  → flatter curve, harder mobs more common
 *   End       : lambda = 50  → even flatter, high levels fairly common
 *
 * Distance from world spawn shifts an additional multiplier on top,
 * making deep exploration genuinely dangerous.
 *
 * Passive mobs (animals) are hard-capped at 30.
 */
public class DifficultyCalculator {

    private static final Random RNG = new Random();

    // Exponential decay lambda per dimension (higher = flatter / harder)
    private static final double OVERWORLD_LAMBDA = 20.0;
    private static final double NETHER_LAMBDA    = 35.0;
    private static final double END_LAMBDA       = 50.0;

    // Max attempts before giving up and returning 0 (avoids infinite loop)
    private static final int MAX_ATTEMPTS = 200;

    // Difficulty bonus per 500 blocks from world spawn (overworld only)
    // Implemented as a reduction to lambda (flatter curve = harder mobs)
    private static final double DISTANCE_LAMBDA_REDUCTION_PER_500 = 2.5;
    private static final double MIN_OVERWORLD_LAMBDA               = 10.0;

    private static final double PASSIVE_MOB_CAP = 30.0;

    /**
     * Rolls a difficulty level for the given entity based on its position and dimension.
     * Returns a value in [0, 100] with exponential probability decay.
     */
    public static double roll(LivingEntity entity) {
        ResourceKey<Level> dim = entity.level().dimension();
        double lambda;

        if (dim.equals(Level.NETHER)) {
            lambda = NETHER_LAMBDA;
        } else if (dim.equals(Level.END)) {
            lambda = END_LAMBDA;
        } else {
            // Overworld: the further from spawn, the flatter the curve (more hard mobs)
            double reduction = distanceLambdaReduction(entity);
            lambda = Math.max(OVERWORLD_LAMBDA - reduction, MIN_OVERWORLD_LAMBDA);
        }

        double level = exponentialSample(lambda);

        if (isPassive(entity)) {
            level = Math.min(level, PASSIVE_MOB_CAP);
        }

        return level;
    }

    // ── Core sampling ──────────────────────────────────────────────────────

    /**
     * Rejection sampling: draw uniform [0,100], accept with P = e^(-x/lambda).
     * This produces a true exponential distribution over [0, 100].
     */
    private static double exponentialSample(double lambda) {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            double candidate = RNG.nextDouble() * 100.0;          // uniform [0, 100]
            double acceptance = Math.exp(-candidate / lambda);     // e^(-x/lambda) in [0,1]
            if (RNG.nextDouble() < acceptance) {
                return candidate;
            }
        }
        // Fallback: return a low value (extremely unlikely to reach here)
        return RNG.nextDouble() * 10.0;
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private static double distanceLambdaReduction(LivingEntity entity) {
        BlockPos pos = entity.blockPosition();
        double dist = Math.sqrt(pos.getX() * (double) pos.getX() + pos.getZ() * (double) pos.getZ());
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
