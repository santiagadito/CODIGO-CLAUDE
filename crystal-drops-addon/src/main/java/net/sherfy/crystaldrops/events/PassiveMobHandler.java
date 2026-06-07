package net.sherfy.crystaldrops.events;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystal_leveling.init.CrystalLevelingModAttributes;
import net.sherfy.crystaldrops.CrystalDropsMod;

/**
 * Ensures passive mobs (animals) have zero difficulty and no gear.
 *
 * Crystal Leveling assigns a difficulty_level to all entities on spawn.
 * This handler resets that attribute to 0 for passive mobs and strips
 * any armor or weapons they may have received.
 */
@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PassiveMobHandler {

    private static final EquipmentSlot[] ALL_SLOTS = {
        EquipmentSlot.HEAD,
        EquipmentSlot.CHEST,
        EquipmentSlot.LEGS,
        EquipmentSlot.FEET,
        EquipmentSlot.MAINHAND,
        EquipmentSlot.OFFHAND
    };

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof LivingEntity living)) return;
        if (!isPassive(living)) return;

        // Force difficulty_level to 0
        AttributeInstance attr = living.getAttribute(
            CrystalLevelingModAttributes.DIFFICULTY_LEVEL.get());
        if (attr != null && attr.getBaseValue() != 0) {
            attr.setBaseValue(0);
            CrystalDropsMod.LOGGER.debug("[PassiveMob] Reset difficulty_level to 0 on {}",
                living.getName().getString());
        }

        // Strip all gear
        for (EquipmentSlot slot : ALL_SLOTS) {
            if (!living.getItemBySlot(slot).isEmpty()) {
                living.setItemSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    public static boolean isPassive(LivingEntity entity) {
        return entity instanceof Animal || entity instanceof AgeableMob;
    }
}
