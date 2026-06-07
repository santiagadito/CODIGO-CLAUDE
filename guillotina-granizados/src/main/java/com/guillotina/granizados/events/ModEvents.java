package com.guillotina.granizados.events;

import com.guillotina.granizados.GranizadosMod;
import com.guillotina.granizados.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Drops de ingredientes por tier (CrystalDrops):
 *
 *  WEAK / COMMON / COMMON+  → 50%  de soltar 1 ingrediente aleatorio (1 unidad)
 *  UNCOMMON / RARE          → 75%  de soltar 2 ingredientes distintos aleatorios (2 de cada uno)
 *  LEGENDARY                → 100% suelta los 4 ingredientes (2 de cada uno)
 */
@Mod.EventBusSubscriber(modid = GranizadosMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    private static final List<Item> ALL_INGREDIENTS = List.of(
            ModItems.MEZCLA_QUIPITOS.get(),
            ModItems.MEZCLA_REVOLCON.get(),
            ModItems.TAMARINDO.get(),
            ModItems.MEZCLA_BOMBON.get()
    );

    private static Method rollMethod     = null;
    private static Method tierNameMethod = null;

    static {
        try {
            Class<?> calc  = Class.forName("net.sherfy.crystaldrops.init.DifficultyCalculator");
            rollMethod     = calc.getMethod("roll",     LivingEntity.class);
            tierNameMethod = calc.getMethod("tierName", double.class);
        } catch (ClassNotFoundException ignored) {
            // CrystalDrops no cargado — fallback vanilla
        } catch (Exception e) {
            System.err.println("[GranizadosMod] Error enlazando CrystalDrops: " + e.getMessage());
        }
    }

    @SubscribeEvent
    public static void onMobDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Monster)) return;
        if (entity.level().isClientSide()) return;

        String tier = resolveTier(entity);
        RandomSource rng = entity.getRandom();

        switch (tier) {

            // ── WEAK / COMMON / COMMON+: 50% → 1 ingrediente aleatorio (x1) ──
            case "WEAK", "COMMON", "COMMON+" -> {
                if (rng.nextFloat() < 0.50f) {
                    Item ingredient = pickRandom(ALL_INGREDIENTS, rng);
                    spawnDrop(event, entity, ingredient, 1);
                }
            }

            // ── UNCOMMON / RARE: 75% → 2 ingredientes distintos (x2 c/u) ─────
            case "UNCOMMON", "RARE" -> {
                if (rng.nextFloat() < 0.75f) {
                    List<Item> shuffled = shuffled(ALL_INGREDIENTS, rng);
                    spawnDrop(event, entity, shuffled.get(0), 2);
                    spawnDrop(event, entity, shuffled.get(1), 2);
                }
            }

            // ── LEGENDARY: 100% → los 4 ingredientes (x2 c/u) ───────────────
            case "LEGENDARY" -> {
                for (Item ingredient : ALL_INGREDIENTS) {
                    spawnDrop(event, entity, ingredient, 2);
                }
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static String resolveTier(LivingEntity entity) {
        if (rollMethod != null && tierNameMethod != null) {
            try {
                double diff = (double) rollMethod.invoke(null, entity);
                return (String) tierNameMethod.invoke(null, diff);
            } catch (Exception ignored) {}
        }
        return switch (entity.level().getDifficulty()) {
            case EASY   -> "COMMON";
            case NORMAL -> "UNCOMMON";
            case HARD   -> "RARE";
            default     -> "WEAK";
        };
    }

    private static Item pickRandom(List<Item> items, RandomSource rng) {
        return items.get(rng.nextInt(items.size()));
    }

    private static List<Item> shuffled(List<Item> items, RandomSource rng) {
        List<Item> copy = new ArrayList<>(items);
        // Fisher-Yates con RandomSource
        for (int i = copy.size() - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            Item tmp = copy.get(i);
            copy.set(i, copy.get(j));
            copy.set(j, tmp);
        }
        return copy;
    }

    private static void spawnDrop(LivingDropsEvent event, LivingEntity entity,
                                   Item item, int count) {
        ItemEntity ie = new ItemEntity(
                entity.level(),
                entity.getX(), entity.getY(), entity.getZ(),
                new ItemStack(item, count)
        );
        ie.setDefaultPickUpDelay();
        event.getDrops().add(ie);
    }
}
