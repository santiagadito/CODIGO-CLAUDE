package net.sherfy.crystaldrops.compat;

import net.minecraftforge.fml.ModList;

/**
 * Detects which companion mods are loaded and exposes flags used throughout
 * Crystal Drops to avoid stacking effects that would push difficulty too high.
 *
 * Target combined server difficulty: 75 / 100
 *   - Dangerous Forge contributes ~45%  (health scaling, gear, creeper/spider buffs)
 *   - Crystal Drops  contributes ~30%  (difficulty tiers, frenzy, tiered loot)
 */
public final class ModCompatibility {

    /** True if Dangerous Forge (modid "dangerous") is present. */
    public static final boolean DANGEROUS_LOADED =
        ModList.get().isLoaded("dangerous");

    /** True if Crystal Leveling (modid "crystal_leveling") is present. */
    public static final boolean CRYSTAL_LEVELING_LOADED =
        ModList.get().isLoaded("crystal_leveling");

    private ModCompatibility() {}

    /**
     * When Dangerous is loaded, it independently boosts creeper and spider speed
     * via attribute modifiers. Crystal Drops must skip Speed effects on these
     * mob types to prevent stacking beyond the 75% difficulty target.
     */
    public static boolean shouldSkipSpeedFor(net.minecraft.world.entity.LivingEntity entity) {
        if (!DANGEROUS_LOADED) return false;
        return entity instanceof net.minecraft.world.entity.monster.Creeper
            || entity instanceof net.minecraft.world.entity.monster.Spider;
    }

    /**
     * When Dangerous is loaded it equips mobs with gear via EntityJoinLevelEvent.
     * Crystal Leveling may also equip gear. To avoid triple-layered armor,
     * our PassiveMobHandler strips passives on join — which is safe since
     * Dangerous only targets Monster subclasses.
     *
     * For hostile mobs, we do NOT strip gear so Dangerous gear is preserved.
     * Crystal Leveling gear on top of Dangerous gear can stack; this is acceptable
     * since Crystal Leveling is our difficulty-level source.
     */
    public static boolean isDangerousGearPresent(net.minecraft.world.item.ItemStack stack) {
        // Dangerous marks its items with an NBT tag "dangerous_gear" = true
        return DANGEROUS_LOADED
            && stack.hasTag()
            && stack.getTag().contains("dangerous_gear");
    }
}
