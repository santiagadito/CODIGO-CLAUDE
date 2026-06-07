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
 *
 * We only use ExplosionEvent.Detonate — cancelling Start also cancels
 * entity damage, so we let the explosion run normally and intercept
 * at Detonate to strip blocks and filter entities.
 */
@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ScrollTntHandler {

    private static final Map<UUID, UUID> SCROLL_TNTS = new ConcurrentHashMap<>();

    public static void registerScrollTnt(UUID tntId, UUID invokerId) {
        SCROLL_TNTS.put(tntId, invokerId);
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        Explosion explosion = event.getExplosion();
        Entity source = explosion.getDirectSourceEntity();
        if (!(source instanceof PrimedTnt tnt)) return;
        if (!SCROLL_TNTS.containsKey(tnt.getUUID())) return;

        SCROLL_TNTS.remove(tnt.getUUID());

        // No block destruction
        event.getAffectedBlocks().clear();

        // Only non-player monsters take damage
        event.getAffectedEntities().removeIf(entity ->
            !(entity instanceof Monster) || entity instanceof Player
        );

        CrystalDropsMod.LOGGER.debug("[AirStrike] Explosion filtered — monsters only, no blocks");
    }
}
