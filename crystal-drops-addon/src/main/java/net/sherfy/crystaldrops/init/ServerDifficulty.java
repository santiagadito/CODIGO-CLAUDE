package net.sherfy.crystaldrops.init;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sherfy.crystaldrops.CrystalDropsMod;

/**
 * Global difficulty multiplier for the whole server.
 *
 * 1.0 = baseline (100%). Higher values make high-level mobs spawn more often
 * AND make every mob's stat scaling (damage/health/armor) stronger. Lower
 * values calm everything down.
 *
 * Controlled at runtime via {@code /serverdifficulty}. Persisted to the
 * overworld save data so it survives restarts.
 */
@Mod.EventBusSubscriber(modid = CrystalDropsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ServerDifficulty {

    public static final double MIN = 0.25;  //  25%
    public static final double MAX = 3.0;   // 300%

    private static final String DATA_NAME = "crystaldrops_difficulty";
    private static volatile double multiplier = 1.0;

    private ServerDifficulty() {}

    public static double getMultiplier() {
        return multiplier;
    }

    public static double clamp(double v) {
        return Math.max(MIN, Math.min(MAX, v));
    }

    public static void setMultiplier(MinecraftServer server, double value) {
        multiplier = clamp(value);
        if (server != null) {
            Data data = server.overworld().getDataStorage()
                .computeIfAbsent(Data::load, Data::new, DATA_NAME);
            data.value = multiplier;
            data.setDirty();
        }
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ServerLevel overworld = event.getServer().overworld();
        Data data = overworld.getDataStorage()
            .computeIfAbsent(Data::load, Data::new, DATA_NAME);
        multiplier = clamp(data.value);
        CrystalDropsMod.LOGGER.info("[ServerDifficulty] loaded multiplier = {}%",
            Math.round(multiplier * 100));
    }

    // ── Persistence ──────────────────────────────────────────────────────────

    public static class Data extends SavedData {
        private double value = 1.0;

        public static Data load(CompoundTag tag) {
            Data d = new Data();
            d.value = tag.getDouble("multiplier");
            if (d.value <= 0) d.value = 1.0;
            return d;
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            tag.putDouble("multiplier", value);
            return tag;
        }
    }
}
