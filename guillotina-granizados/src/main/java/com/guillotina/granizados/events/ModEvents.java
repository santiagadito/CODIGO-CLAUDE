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
import java.util.List;

/**
 * Drops de ingredientes por tier (CrystalDrops):
 *
 *  WEAK      → 25%  de soltar 1 ingrediente aleatorio (x1)
 *  COMMON    → 40%  de soltar 1 ingrediente aleatorio (x1)
 *  COMMON+   → 60%  de soltar 1 ingrediente aleatorio (x1)
 *  UNCOMMON  → 72%  de soltar 2 ingredientes distintos (x1 c/u)
 *  RARE      → 85%  de soltar 2 ingredientes distintos (x2 c/u)
 *  LEGENDARY → 92%  de soltar 3 ingredientes distintos aleatorios (x2 c/u)
 */
@Mod.EventBusSubscriber(modid = GranizadosMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    // Lazy: los RegistryObject.get() solo se llaman cuando ya están registrados
    private static List<Item> getAllIngredients() {
        return List.of(
                ModItems.MEZCLA_QUIPITOS.get(),
                ModItems.MEZCLA_REVOLCON.get(),
                ModItems.TAMARINDO.get(),
                ModItems.MEZCLA_BOMBON.get()
        );
    }

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

            // ── WEAK: 25% → 1 ingrediente aleatorio (x1) ────────────────────
            case "WEAK" -> {
                if (rng.nextFloat() < 0.25f) {
                    spawnDrop(event, entity, pickRandom(getAllIngredients(), rng), 1);
                }
            }

            // ── COMMON: 40% → 1 ingrediente aleatorio (x1) ──────────────────
            case "COMMON" -> {
                if (rng.nextFloat() < 0.40f) {
                    spawnDrop(event, entity, pickRandom(getAllIngredients(), rng), 1);
                }
            }

            // ── COMMON+: 60% → 1 ingrediente aleatorio (x1) ─────────────────
            case "COMMON+" -> {
                if (rng.nextFloat() < 0.60f) {
                    spawnDrop(event, entity, pickRandom(getAllIngredients(), rng), 1);
                }
            }

            // ── UNCOMMON: 72% → 2 ingredientes distintos (x1 c/u) ───────────
            case "UNCOMMON" -> {
                if (rng.nextFloat() < 0.72f) {
                    List<Item> s = shuffled(getAllIngredients(), rng);
                    spawnDrop(event, entity, s.get(0), 1);
                    spawnDrop(event, entity, s.get(1), 1);
                }
            }

            // ── RARE: 85% → 2 ingredientes distintos (x2 c/u) ───────────────
            case "RARE" -> {
                if (rng.nextFloat() < 0.85f) {
                    List<Item> s = shuffled(getAllIngredients(), rng);
                    spawnDrop(event, entity, s.get(0), 2);
                    spawnDrop(event, entity, s.get(1), 2);
                }
            }

            // ── LEGENDARY: 92% → 3 ingredientes distintos aleatorios (x2 c/u)
            case "LEGENDARY" -> {
                if (rng.nextFloat() < 0.92f) {
                    List<Item> s = shuffled(getAllIngredients(), rng);
                    spawnDrop(event, entity, s.get(0), 2);
                    spawnDrop(event, entity, s.get(1), 2);
                    spawnDrop(event, entity, s.get(2), 2);
                }
            }
        }
    }

    // ── API pública para testeo ───────────────────────────────────────────────

    public static List<Item> simulateDrop(String tier, RandomSource rng) {
        List<Item> result = new ArrayList<>();
        switch (tier) {
            case "WEAK" -> { if (rng.nextFloat() < 0.25f) result.add(pickRandom(getAllIngredients(), rng)); }
            case "COMMON" -> { if (rng.nextFloat() < 0.40f) result.add(pickRandom(getAllIngredients(), rng)); }
            case "COMMON+" -> { if (rng.nextFloat() < 0.60f) result.add(pickRandom(getAllIngredients(), rng)); }
            case "UNCOMMON" -> {
                if (rng.nextFloat() < 0.72f) {
                    List<Item> s = shuffled(getAllIngredients(), rng);
                    result.add(s.get(0)); result.add(s.get(1));
                }
            }
            case "RARE" -> {
                if (rng.nextFloat() < 0.85f) {
                    List<Item> s = shuffled(getAllIngredients(), rng);
                    result.add(s.get(0)); result.add(s.get(1));
                }
            }
            case "LEGENDARY" -> {
                if (rng.nextFloat() < 0.92f) {
                    List<Item> s = shuffled(getAllIngredients(), rng);
                    result.add(s.get(0)); result.add(s.get(1)); result.add(s.get(2));
                }
            }
        }
        return result;
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
