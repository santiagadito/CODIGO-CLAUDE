package net.sherfy.crystaldrops.events;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystaldrops.CrystalDropsMod;

/**
 * Strips armor and held items from passive mobs (animals) when they join the world.
 *
 * Crystal Leveling may equip armor based on difficulty_level, but passive mobs
 * (cows, sheep, pigs, etc.) should never wear gear — it looks wrong and is unbalanced
 * since they are capped at difficulty 30.
 */
@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PassiveMobHandler {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
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

        boolean hadGear = false;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (!living.getItemBySlot(slot).isEmpty()) {
                living.setItemSlot(slot, ItemStack.EMPTY);
                hadGear = true;
            }
        }

        if (hadGear) {
            CrystalDropsMod.LOGGER.debug("[PassiveMob] Stripped gear from {}",
                living.getName().getString());
        }
    }

    private static boolean isPassive(LivingEntity entity) {
        return entity instanceof Animal || entity instanceof AgeableMob;
    }
}
