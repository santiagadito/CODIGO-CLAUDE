package net.sherfy.crystaldrops.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystaldrops.CrystalDropsMod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Intercepts explosions from Summon Scroll TNTs and applies these rules:
 *   1. Block destruction is cancelled entirely.
 *   2. Only Monster entities receive damage.
 *   3. Players (including the invoker) are immune.
 */
@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ScrollTntHandler {

    // Maps scroll TNT UUID → invoker player UUID
    private static final Map<UUID, UUID> SCROLL_TNTS = new ConcurrentHashMap<>();

    /** Called by SummonScrollItem when spawning each TNT. */
    public static void registerScrollTnt(UUID tntId, UUID invokerId) {
        SCROLL_TNTS.put(tntId, invokerId);
    }

    @SubscribeEvent
    public static void onExplosionStart(ExplosionEvent.Start event) {
        Explosion explosion = event.getExplosion();
        Entity source = explosion.getDirectSourceEntity();
        if (!(source instanceof PrimedTnt tnt)) return;
        if (!SCROLL_TNTS.containsKey(tnt.getUUID())) return;

        // Cancel the default block-breaking calculation entirely
        event.setCanceled(true);

        // Manually apply entity damage phase (no block damage)
        explosion.finalizeExplosion(false);
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        Explosion explosion = event.getExplosion();
        Entity source = explosion.getDirectSourceEntity();
        if (!(source instanceof PrimedTnt tnt)) return;
        if (!SCROLL_TNTS.containsKey(tnt.getUUID())) return;

        SCROLL_TNTS.remove(tnt.getUUID());

        // Rule 1: no blocks destroyed
        event.getAffectedBlocks().clear();

        // Rule 2 & 3: only non-player monsters take damage
        event.getAffectedEntities().removeIf(entity ->
            !(entity instanceof Monster) || entity instanceof Player
        );
    }
}
