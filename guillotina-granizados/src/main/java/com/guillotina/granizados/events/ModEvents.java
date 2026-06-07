package com.guillotina.granizados.events;

import com.guillotina.granizados.GranizadosMod;
import com.guillotina.granizados.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Method;

/**
 * Escucha la muerte de mobs y añade ingredientes de granizados a los drops
 * según el tier de dificultad del mod CrystalDrops.
 *
 * Tiers (de menor a mayor): WEAK → COMMON → COMMON+ → UNCOMMON → RARE → LEGENDARY
 */
@Mod.EventBusSubscriber(modid = GranizadosMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    // Reflection: acceso a CrystalDrops en tiempo de ejecución (opcional)
    private static Method rollMethod     = null;
    private static Method tierNameMethod = null;

    static {
        try {
            Class<?> calc = Class.forName("net.sherfy.crystaldrops.init.DifficultyCalculator");
            rollMethod     = calc.getMethod("roll",     LivingEntity.class);
            tierNameMethod = calc.getMethod("tierName", double.class);
        } catch (ClassNotFoundException ignored) {
            // CrystalDrops no está cargado — se usará fallback vanilla
        } catch (Exception e) {
            System.err.println("[GranizadosMod] Error enlazando CrystalDrops: " + e.getMessage());
        }
    }

    @SubscribeEvent
    public static void onMobDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();

        // Solo mobs hostiles
        if (!(entity instanceof Monster)) return;
        if (entity.level().isClientSide()) return;

        String tier = resolveTier(entity);
        RandomSource rng = entity.getRandom();

        // ── Probabilidades por tier ─────────────────────────────────────
        //                                  WEAK   COM  COM+  UNC  RARE   LEG
        addDrop(event, entity, rng, ModItems.MEZCLA_QUIPITOS.get(),
                chanceFor(tier,                0.00f, 0.06f, 0.12f, 0.22f, 0.38f, 0.55f));

        addDrop(event, entity, rng, ModItems.MEZCLA_REVOLCON.get(),
                chanceFor(tier,                0.00f, 0.00f, 0.06f, 0.14f, 0.26f, 0.42f));

        addDrop(event, entity, rng, ModItems.TAMARINDO.get(),
                chanceFor(tier,                0.00f, 0.00f, 0.00f, 0.08f, 0.18f, 0.32f));

        addDrop(event, entity, rng, ModItems.MEZCLA_BOMBON.get(),
                chanceFor(tier,                0.00f, 0.00f, 0.00f, 0.00f, 0.10f, 0.22f));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Obtiene el tier de CrystalDrops o lo aproxima desde dificultad vanilla. */
    private static String resolveTier(LivingEntity entity) {
        if (rollMethod != null && tierNameMethod != null) {
            try {
                double diff = (double) rollMethod.invoke(null, entity);
                return (String) tierNameMethod.invoke(null, diff);
            } catch (Exception ignored) {}
        }
        // Fallback a dificultad vanilla de Minecraft
        return switch (entity.level().getDifficulty()) {
            case EASY   -> "COMMON";
            case NORMAL -> "UNCOMMON";
            case HARD   -> "RARE";
            default     -> "WEAK"; // PEACEFUL
        };
    }

    private static float chanceFor(String tier,
                                    float weak, float common, float commonPlus,
                                    float uncommon, float rare, float legendary) {
        return switch (tier) {
            case "WEAK"      -> weak;
            case "COMMON"    -> common;
            case "COMMON+"   -> commonPlus;
            case "UNCOMMON"  -> uncommon;
            case "RARE"      -> rare;
            case "LEGENDARY" -> legendary;
            default          -> 0f;
        };
    }

    private static void addDrop(LivingDropsEvent event, LivingEntity entity,
                                 RandomSource rng, Item item, float chance) {
        if (chance > 0f && rng.nextFloat() < chance) {
            ItemEntity ie = new ItemEntity(
                    entity.level(),
                    entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(item)
            );
            ie.setDefaultPickUpDelay();
            event.getDrops().add(ie);
        }
    }
}
