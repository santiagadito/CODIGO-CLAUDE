package net.sherfy.crystaldrops.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystal_leveling.init.CrystalLevelingModAttributes;
import net.sherfy.crystaldrops.CrystalDropsMod;

@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TieredDropsHandler {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();

        // Players don't drop tiered loot
        if (entity instanceof net.minecraft.world.entity.player.Player) return;

        // Read difficulty_level from Crystal Leveling mod (default 0)
        double difficulty = 0;
        AttributeInstance attr = entity.getAttribute(CrystalLevelingModAttributes.DIFFICULTY_LEVEL.get());
        if (attr != null) {
            difficulty = attr.getBaseValue();
        }

        ItemStack drop = getTieredDrop(difficulty);
        entity.spawnAtLocation(drop);

        CrystalDropsMod.LOGGER.debug("Entity {} (difficulty={}) dropped {}",
            entity.getName().getString(), difficulty, drop.getItem());
    }

    /**
     * Returns the loot item based on difficulty tier.
     *
     * Tiers:
     *  81-100 : Nether Star  (top / colorful)
     *  65-80  : Diamond      (dark blue)
     *  49-64  : Emerald      (light purple)
     *  33-48  : Iron Ingot   (red)
     *  17-32  : Gold Ingot   (yellow)
     *  0-16   : Apple        (green / baseline)
     */
    private static ItemStack getTieredDrop(double difficulty) {
        if (difficulty >= 81) return new ItemStack(Items.NETHER_STAR);
        if (difficulty >= 65) return new ItemStack(Items.DIAMOND);
        if (difficulty >= 49) return new ItemStack(Items.EMERALD);
        if (difficulty >= 33) return new ItemStack(Items.IRON_INGOT);
        if (difficulty >= 17) return new ItemStack(Items.GOLD_INGOT);
        return new ItemStack(Items.APPLE);
    }
}
