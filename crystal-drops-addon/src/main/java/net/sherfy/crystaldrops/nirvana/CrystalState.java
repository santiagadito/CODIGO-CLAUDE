package net.sherfy.crystaldrops.nirvana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystaldrops.CrystalDropsMod;
import net.sherfy.crystaldrops.init.ServerDifficulty;

/**
 * Global, persisted state for the Crystal × Nirvana integration:
 *
 *  • awakened     — Crystal's difficulty systems stay DORMANT until the first
 *                   "LA FINCA" achievement is unlocked anywhere on the server.
 *  • Peace Day    — when the whole server smokes during the dusk window, the
 *                   difficulty drops to a calm baseline for one full MC day.
 */
@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CrystalState {

    /** Difficulty during Peace Day: the "pre-Crystal" baseline (vanilla) + 25%. */
    public static final double PEACE_DAY_MULTIPLIER = 0.25;
    public static final long   PEACE_DAY_TICKS      = 24000L; // one full MC day

    private static final String DATA_NAME = "crystaldrops_state";

    private static volatile boolean awakened           = false;
    private static volatile long    peaceDayEndTick    = -1L;
    private static volatile double  prePeaceMultiplier = 1.0;

    private CrystalState() {}

    // ── Awakening (gate for all difficulty systems) ──────────────────────────

    public static boolean isAwakened() {
        return awakened;
    }

    public static void awaken(MinecraftServer server) {
        if (awakened) return;
        awakened = true;
        save(server);
        CrystalDropsMod.LOGGER.info("[CrystalState] Crystal has AWAKENED — difficulty is now active.");
        ChatBanner.broadcastAwaken(server);
    }

    // ── Peace Day ────────────────────────────────────────────────────────────

    public static boolean isPeaceDayActive() {
        return peaceDayEndTick > 0;
    }

    public static long getPeaceDayEndTick() {
        return peaceDayEndTick;
    }

    public static void startPeaceDay(MinecraftServer server) {
        if (isPeaceDayActive()) return;
        prePeaceMultiplier = ServerDifficulty.getMultiplier();
        long now = server.overworld().getGameTime();
        peaceDayEndTick = now + PEACE_DAY_TICKS;
        ServerDifficulty.setMultiplier(server, PEACE_DAY_MULTIPLIER);
        save(server);
        CrystalDropsMod.LOGGER.info("[CrystalState] PEACE DAY started until tick {}", peaceDayEndTick);
        ChatBanner.broadcastPeaceDay(server);
    }

    public static void endPeaceDay(MinecraftServer server) {
        if (!isPeaceDayActive()) return;
        peaceDayEndTick = -1L;
        ServerDifficulty.setMultiplier(server, prePeaceMultiplier);
        save(server);
        CrystalDropsMod.LOGGER.info("[CrystalState] Peace Day ended — difficulty restored to {}%",
            Math.round(prePeaceMultiplier * 100));
        ChatBanner.broadcastPeaceDayEnd(server, prePeaceMultiplier);
    }

    // ── Persistence ──────────────────────────────────────────────────────────

    private static Data data(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage().computeIfAbsent(Data::load, Data::new, DATA_NAME);
    }

    private static void save(MinecraftServer server) {
        Data d = data(server);
        d.awakened           = awakened;
        d.peaceDayEndTick    = peaceDayEndTick;
        d.prePeaceMultiplier = prePeaceMultiplier;
        d.setDirty();
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        Data d = data(event.getServer());
        awakened           = d.awakened;
        peaceDayEndTick    = d.peaceDayEndTick;
        prePeaceMultiplier = d.prePeaceMultiplier <= 0 ? 1.0 : d.prePeaceMultiplier;
        CrystalDropsMod.LOGGER.info("[CrystalState] loaded | awakened={} | peaceDayActive={}",
            awakened, isPeaceDayActive());
    }

    public static class Data extends SavedData {
        private boolean awakened           = false;
        private long    peaceDayEndTick    = -1L;
        private double  prePeaceMultiplier = 1.0;

        public static Data load(CompoundTag tag) {
            Data d = new Data();
            d.awakened           = tag.getBoolean("awakened");
            d.peaceDayEndTick    = tag.getLong("peaceDayEndTick");
            d.prePeaceMultiplier = tag.contains("prePeaceMultiplier") ? tag.getDouble("prePeaceMultiplier") : 1.0;
            return d;
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            tag.putBoolean("awakened", awakened);
            tag.putLong("peaceDayEndTick", peaceDayEndTick);
            tag.putDouble("prePeaceMultiplier", prePeaceMultiplier);
            return tag;
        }
    }
}
