package net.sherfy.crystaldrops.events;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystal_leveling.init.CrystalLevelingModAttributes;
import net.sherfy.crystaldrops.CrystalDropsMod;
import net.sherfy.crystaldrops.init.DifficultyCalculator;
import net.sherfy.crystaldrops.init.MobLootTable;

import java.util.List;

@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TieredDropsHandler {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();

        // Players don't generate tiered loot
        if (entity instanceof Player) return;
        // Server-side only
        if (level.isClientSide()) return;

        // ── 1. Resolve difficulty_level ──────────────────────────────────
        double difficulty = resolveDifficulty(entity);

        // ── 2. Spawn tiered item drops ───────────────────────────────────
        List<ItemStack> drops = MobLootTable.getDrops(entity, difficulty);
        drops.addAll(MobLootTable.getSummonScrollDrops(difficulty));
        for (ItemStack stack : drops) {
            if (!stack.isEmpty()) {
                entity.spawnAtLocation(stack);
            }
        }

        // ── 3. Spawn bonus XP orb ────────────────────────────────────────
        int bonusXp = MobLootTable.bonusXp(difficulty);
        if (bonusXp > 0) {
            ExperienceOrb.award(
                (net.minecraft.server.level.ServerLevel) level,
                entity.position(),
                bonusXp
            );
        }

        CrystalDropsMod.LOGGER.debug(
            "[CrystalDrops] {} | tier={} | difficulty={} | drops={} | xp+{}",
            entity.getName().getString(),
            DifficultyCalculator.tierName(difficulty),
            String.format("%.1f", difficulty),
            drops.size(),
            bonusXp
        );
    }

    /**
     * Reads difficulty_level from Crystal Leveling attribute.
     * Falls back to rolling a new value if the attribute is missing or zero
     * (safety net for mobs that spawned before the mod was loaded).
     */
    private static double resolveDifficulty(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(
            CrystalLevelingModAttributes.DIFFICULTY_LEVEL.get()
        );

        if (attr != null && attr.getBaseValue() > 0) {
            return attr.getBaseValue();
        }

        // Fallback: roll on the spot using position/dimension
        double rolled = DifficultyCalculator.roll(entity);
        CrystalDropsMod.LOGGER.debug(
            "[CrystalDrops] No difficulty_level on {} — rolled {}",
            entity.getName().getString(),
            String.format("%.1f", rolled)
        );
        return rolled;
    }
}
