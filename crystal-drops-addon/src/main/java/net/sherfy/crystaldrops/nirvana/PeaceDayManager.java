package net.sherfy.crystaldrops.nirvana;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.sherfy.crystaldrops.CrystalDropsMod;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Drives the Peace Day mechanic (#2 + #3):
 *
 *  • The "dusk window" runs from sunset (~12000) to nightfall (~13800).
 *  • Only smoking during that window counts.
 *  • If EVERY online (non-spectator) player smokes within the same window,
 *    Peace Day triggers: difficulty drops for a full MC day.
 */
@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PeaceDayManager {

    private static final long WINDOW_START = 12000L; // sunset
    private static final long WINDOW_END   = 13800L; // nightfall

    private static final Set<UUID> SMOKERS = new HashSet<>();
    private static boolean windowWasOpen = false;

    private PeaceDayManager() {}

    public static boolean isSmokeWindow(ServerLevel level) {
        long t = level.getDayTime() % 24000L;
        return t >= WINDOW_START && t <= WINDOW_END;
    }

    public static synchronized void registerSmoker(ServerPlayer player) {
        if (!CrystalState.isAwakened()) return;     // dormant Crystal → no effect
        if (CrystalState.isPeaceDayActive()) return;
        SMOKERS.add(player.getUUID());
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
            "§a✔ §7Fumaste en la hora de la paz. §8(" + SMOKERS.size() + " jugadores)"));
        checkAllSmoked(player.server);
    }

    private static void checkAllSmoked(MinecraftServer server) {
        var players = server.getPlayerList().getPlayers();
        if (players.isEmpty()) return;

        for (ServerPlayer p : players) {
            if (p.isSpectator()) continue;
            if (!SMOKERS.contains(p.getUUID())) return; // someone hasn't smoked yet
        }
        // Everyone online smoked → Peace Day!
        CrystalState.startPeaceDay(server);
        SMOKERS.clear();
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        if ((server.getTickCount() % 20) != 0) return;  // once per second

        ServerLevel overworld = server.overworld();

        // End Peace Day after a full day.
        if (CrystalState.isPeaceDayActive()
            && overworld.getGameTime() >= CrystalState.getPeaceDayEndTick()) {
            CrystalState.endPeaceDay(server);
        }

        // Announce window open / reset smokers on window edges.
        boolean open = isSmokeWindow(overworld);
        if (open && !windowWasOpen) {
            SMOKERS.clear();
            if (CrystalState.isAwakened() && !CrystalState.isPeaceDayActive()) {
                ChatBanner.broadcastSmokeWindowOpen(server);
            }
        } else if (!open && windowWasOpen) {
            SMOKERS.clear();
        }
        windowWasOpen = open;
    }
}
