package net.sherfy.crystaldrops.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystal_leveling.init.CrystalLevelingModAttributes;
import net.sherfy.crystaldrops.CrystalDropsMod;

/**
 * Multiplies a mob's ORIGINAL (vanilla) drops according to its difficulty_level.
 *
 * This only touches drops that flow through the normal loot path
 * (LivingDropsEvent) — i.e. rotten flesh, bones, gunpowder, string, etc.
 * The custom tiered loot we add ourselves is spawned separately in
 * {@link TieredDropsHandler} via spawnAtLocation, so it is NOT affected here.
 * That keeps the "añadiduras" untouched while the base loot scales.
 *
 * Multiplier by difficulty:
 *   1 + floor(difficulty / 20)   →   diff  20→x2, 40→x3, 60→x4, 80→x5, 100→x6
 * Each stack is capped at its max stack size.
 */
@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VanillaDropMultiplierHandler {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) return;
        if (entity.level().isClientSide()) return;
        if (!net.sherfy.crystaldrops.nirvana.CrystalState.isAwakened()) return; // dormant

        double difficulty = getDifficulty(entity);
        if (difficulty <= 0) return;

        int multiplier = 1 + (int) (Math.min(difficulty, 100.0) / 20.0);
        if (multiplier <= 1) return;

        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (stack.isEmpty()) continue;
            int scaled = Math.min(stack.getMaxStackSize(), stack.getCount() * multiplier);
            stack.setCount(scaled);
        }

        CrystalDropsMod.LOGGER.debug("[VanillaDrops] {} | diff={} | x{}",
            entity.getName().getString(), String.format("%.1f", difficulty), multiplier);
    }

    private static double getDifficulty(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(
            CrystalLevelingModAttributes.DIFFICULTY_LEVEL.get());
        return attr != null ? attr.getBaseValue() : 0;
    }
}
