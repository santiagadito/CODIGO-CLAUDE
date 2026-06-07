package com.guillotina.granizados;

import com.guillotina.granizados.item.GranizadoItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GranizadosMod.MOD_ID);

    // ── Ingredientes (se craftean en mesa normal) ──────────────────────────

    public static final RegistryObject<Item> MEZCLA_QUIPITOS = ITEMS.register("mezcla_quipitos",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MEZCLA_REVOLCON = ITEMS.register("mezcla_revolcon",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TAMARINDO = ITEMS.register("tamarindo",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MEZCLA_BOMBON = ITEMS.register("mezcla_bombon",
            () -> new Item(new Item.Properties()));

    // ── Granizados (se hacen en la Guillotina) ─────────────────────────────

    // Quipitos: Velocidad 3 (3 min) + Náusea 5 (2 seg) — nombre blanco
    public static final RegistryObject<GranizadoItem> GRANIZADO_QUIPITOS = ITEMS.register("granizado_quipitos",
            () -> new GranizadoItem(new Item.Properties(), 0xE8E8E8,
                    () -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3600, 2),
                    () -> new MobEffectInstance(MobEffects.CONFUSION,       40,   4)));

    // Revolcon: Health Boost 5 (3 min) + Regeneración 5 (7 seg) + Náusea 5 (2 seg) — nombre verde
    public static final RegistryObject<GranizadoItem> GRANIZADO_REVOLCON = ITEMS.register("granizado_revolcon",
            () -> new GranizadoItem(new Item.Properties(), 0x28AA3C,
                    () -> new MobEffectInstance(MobEffects.HEALTH_BOOST,  3600, 4),
                    () -> new MobEffectInstance(MobEffects.REGENERATION,   140, 4),
                    () -> new MobEffectInstance(MobEffects.CONFUSION,       40, 4)));

    // Sminorff de Tamarindo: Fuerza 5 (3 min) + Náusea 5 (2 seg) — nombre rojo
    public static final RegistryObject<GranizadoItem> GRANIZADO_SMINORFF_TAMARINDO = ITEMS.register("granizado_sminorff_tamarindo",
            () -> new GranizadoItem(new Item.Properties(), 0xD22323,
                    () -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600, 4),
                    () -> new MobEffectInstance(MobEffects.CONFUSION,      40, 4)));

    // Bombon: Regeneración 5 (2 min) + Náusea 5 (2 seg) — nombre rosado
    public static final RegistryObject<GranizadoItem> GRANIZADO_BOMBON = ITEMS.register("granizado_bombon",
            () -> new GranizadoItem(new Item.Properties(), 0xF05AA0,
                    () -> new MobEffectInstance(MobEffects.REGENERATION, 2400, 4),
                    () -> new MobEffectInstance(MobEffects.CONFUSION,      40, 4)));
}
