package net.sherfy.crystaldrops.events;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystal_leveling.init.CrystalLevelingModAttributes;
import net.sherfy.crystaldrops.CrystalDropsMod;

import java.util.UUID;

/**
 * Scales mob attack damage proportionally to their difficulty_level.
 *
 * Formula: finalDamage = baseDamage * (1 + bonusMultiplier)
 *   bonusMultiplier = (difficulty / 100) * MAX_BONUS
 *
 * MAX_BONUS = 3.0 means:
 *   difficulty   0 → 1.0x (no change)
 *   difficulty  25 → 1.75x
 *   difficulty  50 → 2.5x
 *   difficulty  75 → 3.25x
 *   difficulty 100 → 4.0x
 *
 * Uses MULTIPLY_BASE so it stacks correctly with other modifiers
 * (e.g. Dangerous Forge weapon bonuses).
 */
@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DamageScalingHandler {

    private static final UUID   MODIFIER_UUID = UUID.fromString("a3f2d1e0-4b5c-6d7e-8f9a-0b1c2d3e4f50");
    private static final String MODIFIER_NAME = "crystaldrops:difficulty_damage_bonus";
    private static final double MAX_BONUS     = 3.0; // at difficulty 100 → 4x base damage

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof LivingEntity living)) return;
        if (!(living instanceof Monster)) return;

        // Read difficulty_level
        AttributeInstance diffAttr = living.getAttribute(
            CrystalLevelingModAttributes.DIFFICULTY_LEVEL.get());
        if (diffAttr == null) return;

        double difficulty = diffAttr.getBaseValue();
        if (difficulty <= 0) return;

        // Read attack damage attribute
        AttributeInstance dmgAttr = living.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmgAttr == null) return;

        // Avoid double-applying if mob is reloaded from NBT with modifier already present
        if (dmgAttr.getModifier(MODIFIER_UUID) != null) return;

        double bonus = (difficulty / 100.0) * MAX_BONUS;

        AttributeModifier modifier = new AttributeModifier(
            MODIFIER_UUID,
            MODIFIER_NAME,
            bonus,
            AttributeModifier.Operation.MULTIPLY_BASE
        );

        dmgAttr.addPermanentModifier(modifier);

        CrystalDropsMod.LOGGER.debug(
            "[DamageScaling] {} | difficulty={} | damage x{}",
            living.getName().getString(),
            String.format("%.1f", difficulty),
            String.format("%.2f", 1.0 + bonus));
    }
}
