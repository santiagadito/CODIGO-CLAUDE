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
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystal_leveling.init.CrystalLevelingModAttributes;
import net.sherfy.crystaldrops.CrystalDropsMod;
import net.sherfy.crystaldrops.compat.ModCompatibility;

@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FrenzyHandler {

    private static final int    FRENZY_DURATION    = 400;
    private static final int    FRENZY_AMPLIFIER   = 1;
    private static final double FRENZY_THRESHOLD   = 80.0;
    private static final double FRENZY_HP_PCT      = 0.5;
    private static final int    PARTICLE_INTERVAL  = 10;

    // ── Frenzy trigger ─────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity instanceof Player) return;
        if (!(entity instanceof Monster)) return;
        if (entity.level().isClientSide()) return;
        if (!net.sherfy.crystaldrops.nirvana.CrystalState.isAwakened()) return;

        double difficulty = getDifficulty(entity);
        if (difficulty < FRENZY_THRESHOLD) return;

        float healthAfter = entity.getHealth() - event.getAmount();
        float halfMax     = entity.getMaxHealth() * (float) FRENZY_HP_PCT;

        boolean crossingThreshold = entity.getHealth() >= halfMax && healthAfter < halfMax;
        if (!crossingThreshold) return;

        boolean alreadyFrenzied =
            entity.hasEffect(MobEffects.MOVEMENT_SPEED) &&
            entity.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() >= FRENZY_AMPLIFIER;
        if (alreadyFrenzied) return;

        if (!ModCompatibility.shouldSkipSpeedFor(entity)) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,
                FRENZY_DURATION, FRENZY_AMPLIFIER, false, true));
        }

        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
            FRENZY_DURATION, FRENZY_AMPLIFIER, false, true));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,
            FRENZY_DURATION, 0, false, true));

        CrystalDropsMod.LOGGER.debug("[Frenzy] {} (difficulty={}) entered frenzy!",
            entity.getName().getString(), String.format("%.1f", difficulty));
    }

    // ── Angry villager particles on hit (LEGENDARY only) ─────────────────

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity instanceof Player) return;
        if (entity.level().isClientSide()) return;
        if (!net.sherfy.crystaldrops.nirvana.CrystalState.isAwakened()) return;

        double difficulty = getDifficulty(entity);
        if (difficulty < 81) return;

        ServerLevel serverLevel = (ServerLevel) entity.level();
        double cx = entity.getX();
        double cy = entity.getY() + entity.getBbHeight();
        double cz = entity.getZ();

        serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER,
            cx, cy + 0.3, cz,
            6, 0.3, 0.2, 0.3, 0.0);
    }

    // ── Particle aura ───────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity instanceof Player) return;
        if (entity.level().isClientSide()) return;
        if (!net.sherfy.crystaldrops.nirvana.CrystalState.isAwakened()) return;
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
