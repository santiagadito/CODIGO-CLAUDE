package net.sherfy.crystaldrops.nirvana;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystaldrops.CrystalDropsMod;

import static net.sherfy.crystaldrops.nirvana.CannabisAdvancements.*;

/**
 * Detects the in-game actions that unlock the cannabis achievements and feed
 * the Peace Day mechanic. All detection is decoupled from Nirvana's code.
 */
@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CannabisEventHandler {

    // ── #1.1 LA FINCA — first hemp seed obtained (proxy for "found in loot") ──

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer sp)) return;
        if (sp.tickCount % 40 != 0) return;                // scan ~twice/second
        if (has(sp, LA_FINCA)) return;

        if (inventoryHasSeed(sp)) {
            if (grant(sp, LA_FINCA)) {
                // First achievement anywhere wakes Crystal up (#5).
                CrystalState.awaken(sp.server);
            }
        }
    }

    private static boolean inventoryHasSeed(ServerPlayer sp) {
        for (ItemStack stack : sp.getInventory().items) {
            if (NirvanaRefs.isItem(stack, NirvanaRefs.HEMP_SEEDS)) return true;
        }
        return NirvanaRefs.isItem(sp.getOffhandItem(), NirvanaRefs.HEMP_SEEDS);
    }

    // ── #1.2 SANTA ELENA — planting the first hemp ───────────────────────────

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        if (!NirvanaRefs.isHempBlock(event.getPlacedBlock().getBlock())) return;

        grant(sp, SANTA_ELENA);
    }

    // ── #1.3 POBLADO / #1.5 SOPETRAN — smoking ───────────────────────────────

    @SubscribeEvent
    public static void onUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        ItemStack item = event.getItem();
        if (!NirvanaRefs.isSmokeItem(item)) return;

        onSmoke(sp, NirvanaRefs.isBong(item));
    }

    /** Fallback: the Peace effect appearing on a player means they smoked. */
    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        if (!NirvanaRefs.isPeaceEffect(event.getEffectInstance().getEffect())) return;

        onSmoke(sp, false);
    }

    private static void onSmoke(ServerPlayer sp, boolean fromBong) {
        grant(sp, POBLADO);
        if (fromBong) grant(sp, SOPETRAN);

        // Only smoking during the dusk window counts toward Peace Day (#3).
        ServerLevel level = (ServerLevel) sp.level();
        if (PeaceDayManager.isSmokeWindow(level)) {
            PeaceDayManager.registerSmoker(sp);
        }
    }
}
