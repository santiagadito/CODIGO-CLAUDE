package net.sherfy.crystaldrops.events;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystal_leveling.init.CrystalLevelingModAttributes;
import net.sherfy.crystaldrops.CrystalDropsMod;
import net.sherfy.crystaldrops.compat.ModCompatibility;

/**
 * Handles two visual/combat mechanics for high-difficulty mobs:
 *
 *  FRENZY: mobs with difficulty >= 80 gain Strength II + optional Speed
 *          when their health drops below 50% for the first time.
 *
 *          Compatibility note: if Dangerous Forge is loaded, Speed is skipped
 *          for Creepers and Spiders because that mod already boosts their
 *          movement via attribute modifiers — stacking both would overshoot
 *          the 75% combined difficulty target.
 *
 *  PARTICLES: mobs with difficulty >= 80 emit LAVA + SOUL_FIRE_FLAME
 *             particles every 10 ticks as a danger indicator.
 */
@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FrenzyHandler {

    private static final int    FRENZY_DURATION    = 400;  // 20 seconds
    private static final int    FRENZY_AMPLIFIER   = 1;    // Speed II / Strength II (0-indexed)
    private static final double FRENZY_THRESHOLD   = 80.0;
    private static final double FRENZY_HP_PCT      = 0.5;
    private static final int    PARTICLE_INTERVAL  = 10;   // ticks

    // ── Frenzy trigger ─────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity instanceof Player) return;
        if (!(entity instanceof Monster)) return;
        if (entity.level().isClientSide()) return;

        double difficulty = getDifficulty(entity);
        if (difficulty < FRENZY_THRESHOLD) return;

        // Frenzy fires only the first time health crosses below 50%
        float healthAfter = entity.getHealth() - event.getAmount();
        float halfMax     = entity.getMaxHealth() * (float) FRENZY_HP_PCT;

        boolean crossingThreshold = entity.getHealth() >= halfMax && healthAfter < halfMax;
        if (!crossingThreshold) return;

        // Guard: don't re-apply if already in frenzy
        boolean alreadyFrenzied =
            entity.hasEffect(MobEffects.MOVEMENT_SPEED) &&
            entity.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() >= FRENZY_AMPLIFIER;
        if (alreadyFrenzied) return;

        // ── Compatibility with Dangerous Forge ───────────────────────────
        // Dangerous already boosts Creeper and Spider speed via attributes.
        // Applying Speed II on top would exceed the 75% difficulty target,
        // so we skip Speed for those mob types and only apply Strength.
        if (!ModCompatibility.shouldSkipSpeedFor(entity)) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,
                FRENZY_DURATION, FRENZY_AMPLIFIER, false, true));
        }

        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
            FRENZY_DURATION, FRENZY_AMPLIFIER, false, true));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,
            FRENZY_DURATION, 0, false, true));

        CrystalDropsMod.LOGGER.debug("[Frenzy] {} (difficulty={}) entered frenzy! dangerousCompat={}",
            entity.getName().getString(),
            String.format("%.1f", difficulty),
            ModCompatibility.DANGEROUS_LOADED);
    }

    // ── Particle aura ──────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity instanceof Player) return;
        if (entity.level().isClientSide()) return;
        if (entity.tickCount % PARTICLE_INTERVAL != 0) return;

        double difficulty = getDifficulty(entity);
        if (difficulty < FRENZY_THRESHOLD) return;

        ServerLevel serverLevel = (ServerLevel) entity.level();
        double cx = entity.getX();
        double cy = entity.getY() + entity.getBbHeight() * 0.6;
        double cz = entity.getZ();

        serverLevel.sendParticles(ParticleTypes.LAVA,
            cx, cy, cz, 2, 0.35, 0.4, 0.35, 0.0);

        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
            cx, entity.getY() + 0.1, cz, 3, 0.25, 0.1, 0.25, 0.04);

        if (entity.getHealth() < entity.getMaxHealth() * FRENZY_HP_PCT) {
            serverLevel.sendParticles(ParticleTypes.FLAME,
                cx, cy, cz, 5, 0.4, 0.6, 0.4, 0.06);
        }
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private static double getDifficulty(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(
            CrystalLevelingModAttributes.DIFFICULTY_LEVEL.get());
        return attr != null ? attr.getBaseValue() : 0;
    }
}
