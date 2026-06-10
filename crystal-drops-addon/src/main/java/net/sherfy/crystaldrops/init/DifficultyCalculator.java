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
 * Lambda reference:
 *   Smaller lambda -> steeper decay -> fewer hard mobs (easier)
 *   Larger  lambda -> flatter decay -> more hard mobs  (harder)
 *
 *               With Dangerous   Without Dangerous
 *   Overworld:       10               14
 *   Nether:          16               22
 *   End:             22               30
 *
 * At lambda=10: level 50 accepted ~0.7% of rolls, level 81+ near impossible.
 * Passive mobs always get difficulty 0.
 */
public class DifficultyCalculator {

    private static final Random RNG = new Random();

    // Steeper than before: legendary mobs should be a genuinely rare event.
    private static final double OVERWORLD_LAMBDA_COMPAT = 7.0;
    private static final double NETHER_LAMBDA_COMPAT    = 10.0;
    private static final double END_LAMBDA_COMPAT       = 14.0;

    private static final double OVERWORLD_LAMBDA_SOLO   = 10.0;
    private static final double NETHER_LAMBDA_SOLO      = 14.0;
    private static final double END_LAMBDA_SOLO         = 18.0;

    private static final double DISTANCE_LAMBDA_REDUCTION_PER_500 = 1.5;
    private static final double MIN_OVERWORLD_LAMBDA               = 5.0;

    private static final int MAX_ATTEMPTS = 200;

    public static double roll(LivingEntity entity) {
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

        // Global difficulty knob: a higher multiplier flattens the curve,
        // letting tougher mobs appear more often (and vice-versa).
        lambda *= ServerDifficulty.getMultiplier();

        return exponentialSample(lambda);
    }

    private static double exponentialSample(double lambda) {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            double candidate  = RNG.nextDouble() * 100.0;
            double acceptance = Math.exp(-candidate / lambda);
            if (RNG.nextDouble() < acceptance) {
                return candidate;
            }
        }
        return RNG.nextDouble() * 5.0;
    }

    private static double distanceLambdaReduction(LivingEntity entity) {
        BlockPos pos  = entity.blockPosition();
        double dist   = Math.sqrt(pos.getX() * (double) pos.getX() + pos.getZ() * (double) pos.getZ());
        return (dist / 500.0) * DISTANCE_LAMBDA_REDUCTION_PER_500;
    }

    private static boolean isPassive(LivingEntity entity) {
        return entity instanceof Animal || entity instanceof AgeableMob;
    }

    public static String tierName(double difficulty) {
        if (difficulty >= 81) return "LEGENDARY";
        if (difficulty >= 65) return "RARE";
        if (difficulty >= 49) return "UNCOMMON";
        if (difficulty >= 33) return "COMMON+";
        if (difficulty >= 17) return "COMMON";
        return "WEAK";
    }
}
