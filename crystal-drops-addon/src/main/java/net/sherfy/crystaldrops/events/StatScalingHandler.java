package net.sherfy.crystaldrops.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystal_leveling.init.CrystalLevelingModAttributes;
import net.sherfy.crystaldrops.CrystalDropsMod;
import net.sherfy.crystaldrops.compat.ModCompatibility;
import net.sherfy.crystaldrops.init.ServerDifficulty;

import java.util.UUID;

/**
 * Scales hostile mob stats proportionally to their difficulty_level so that
 * higher difficulty means tougher PvP: more damage, more health, more armor.
 *
 * All bonuses are multiplied by the global {@link ServerDifficulty} knob.
 *
 * ── Dangerous Forge compatibility ──────────────────────────────────────────
 * Dangerous Forge already inflates health and damage (which is what made fights
 * feel "buggy" / over-scaled). When it is loaded we DAMP our own damage and
 * health contribution by {@link #DANGEROUS_DAMP} so the two mods combine into a
 * sane curve instead of multiplying out of control. Armor is left to us since
 * Dangerous mostly grants armor through gear, not the attribute.
 *
 * Scaling at difficulty 100 (global = 100%, solo):
 *   damage  → +120%  (x2.2)
 *   health  → +150%  (x2.5)
 *   armor   → +12 armor points
 * With Dangerous loaded, damage/health bonuses are halved on top of Dangerous'
 * own scaling.
 */
@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StatScalingHandler {

    private static final UUID DAMAGE_UUID = UUID.fromString("a3f2d1e0-4b5c-6d7e-8f9a-0b1c2d3e4f50");
    private static final UUID HEALTH_UUID = UUID.fromString("b4e3c2f1-5c6d-7e8f-9a0b-1c2d3e4f5a61");
    private static final UUID ARMOR_UUID  = UUID.fromString("c5f4d3a2-6d7e-8f9a-0b1c-2d3e4f5a6b72");

    private static final double DAMAGE_MAX_BONUS = 1.20; // +120% at difficulty 100
    private static final double HEALTH_MAX_BONUS = 1.50; // +150% at difficulty 100
    private static final double ARMOR_MAX_POINTS = 12.0; // +12 armor at difficulty 100
    private static final double DANGEROUS_DAMP   = 0.5;  // halve dmg/hp when Dangerous is present

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!net.sherfy.crystaldrops.nirvana.CrystalState.isAwakened()) return; // dormant
        if (!(event.getEntity() instanceof LivingEntity living)) return;
        if (!(living instanceof Monster)) return;

        AttributeInstance diffAttr = living.getAttribute(
            CrystalLevelingModAttributes.DIFFICULTY_LEVEL.get());
        if (diffAttr == null) return;

        double difficulty = diffAttr.getBaseValue();
        if (difficulty <= 0) return;

        double f      = Math.min(difficulty, 100.0) / 100.0;  // 0..1
        double global = ServerDifficulty.getMultiplier();
        double damp   = ModCompatibility.DANGEROUS_LOADED ? DANGEROUS_DAMP : 1.0;

        boolean changed = false;

        // ── Attack damage (multiplicative) ───────────────────────────────────
        AttributeInstance dmg = living.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmg != null && dmg.getModifier(DAMAGE_UUID) == null) {
            double bonus = f * DAMAGE_MAX_BONUS * damp * global;
            dmg.addPermanentModifier(new AttributeModifier(
                DAMAGE_UUID, "crystaldrops:difficulty_damage", bonus,
                AttributeModifier.Operation.MULTIPLY_BASE));
            changed = true;
        }

        // ── Max health (multiplicative, then heal to full) ───────────────────
        AttributeInstance hp = living.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null && hp.getModifier(HEALTH_UUID) == null) {
            double bonus = f * HEALTH_MAX_BONUS * damp * global;
            hp.addPermanentModifier(new AttributeModifier(
                HEALTH_UUID, "crystaldrops:difficulty_health", bonus,
                AttributeModifier.Operation.MULTIPLY_BASE));
            living.setHealth((float) living.getMaxHealth());
            changed = true;
        }

        // ── Armor (flat addition; not damped) ────────────────────────────────
        AttributeInstance armor = living.getAttribute(Attributes.ARMOR);
        if (armor != null && armor.getModifier(ARMOR_UUID) == null) {
            double bonus = f * ARMOR_MAX_POINTS * global;
            armor.addPermanentModifier(new AttributeModifier(
                ARMOR_UUID, "crystaldrops:difficulty_armor", bonus,
                AttributeModifier.Operation.ADDITION));
            changed = true;
        }

        if (changed) {
            CrystalDropsMod.LOGGER.debug(
                "[StatScaling] {} | diff={} | global={}% | dangerous={}",
                living.getName().getString(),
                String.format("%.1f", difficulty),
                Math.round(global * 100),
                ModCompatibility.DANGEROUS_LOADED);
        }
    }
}
