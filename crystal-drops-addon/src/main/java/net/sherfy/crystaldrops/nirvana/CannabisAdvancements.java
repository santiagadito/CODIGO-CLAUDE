package net.sherfy.crystaldrops.nirvana;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sherfy.crystaldrops.CrystalDropsMod;

/**
 * Helper for granting the custom data-driven cannabis advancements from code.
 *
 * Each advancement JSON uses a "minecraft:impossible" criterion named "earned"
 * so it can ONLY be unlocked here, when our event handlers detect the in-game
 * action (finding a seed, planting, smoking, etc.).
 */
public final class CannabisAdvancements {

    public static final String LA_FINCA         = "cannabis/la_finca";
    public static final String SANTA_ELENA      = "cannabis/santa_elena";
    public static final String POBLADO          = "cannabis/poblado";
    public static final String BARRIO_ANTIOQUIA = "cannabis/barrio_antioquia";
    public static final String SOPETRAN         = "cannabis/sopetran";

    private CannabisAdvancements() {}

    public static boolean has(ServerPlayer player, String path) {
        Advancement adv = lookup(player, path);
        if (adv == null) return false;
        return player.getAdvancements().getOrStartProgress(adv).isDone();
    }

    /** Grants the advancement if not already done. Returns true if newly granted. */
    public static boolean grant(ServerPlayer player, String path) {
        Advancement adv = lookup(player, path);
        if (adv == null) {
            CrystalDropsMod.LOGGER.warn("[Cannabis] advancement not found: {}", path);
            return false;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
        if (progress.isDone()) return false;

        boolean granted = false;
        for (String criterion : progress.getRemainingCriteria()) {
            granted |= player.getAdvancements().award(adv, criterion);
        }
        return granted;
    }

    private static Advancement lookup(ServerPlayer player, String path) {
        ResourceLocation id = new ResourceLocation(CrystalDropsMod.MODID, path);
        return player.server.getAdvancements().getAdvancement(id);
    }
}
