package net.sherfy.crystaldrops.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystaldrops.CrystalDropsMod;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Intercepts explosions from Summon Scroll (AIR STRIKE) TNTs:
 *   1. Block destruction is cancelled entirely.
 *   2. EVERY non-player living entity is eliminated, no matter its difficulty
 *      level or how much health Dangerous Forge gave it (guaranteed lethal).
 *   3. Players (including the invoker) are completely immune.
 *
 * We only use ExplosionEvent.Detonate — cancelling Start also cancels
 * entity damage, so we let the explosion run and intercept at Detonate.
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

        // Guaranteed elimination of every non-player entity in range.
        List<Entity> affected = event.getAffectedEntities();
        for (Entity entity : affected) {
            if (entity instanceof Player) continue;
            if (!(entity instanceof LivingEntity living)) continue;

            // Bypass i-frames and deal overwhelming damage so even the toughest
            // (high-difficulty / Dangerous-buffed) mobs die in one strike.
            living.invulnerableTime = 0;
            living.hurt(living.level().damageSources().explosion(explosion), 1.0E6F);
        }

        // Stop the vanilla explosion from re-processing damage/knockback —
        // players stay safe and we've already handled the kills.
        affected.clear();

        CrystalDropsMod.LOGGER.debug("[AirStrike] Wiped all non-player entities, no block damage");
    }
}
