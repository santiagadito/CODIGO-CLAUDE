package com.guillotina.granizados;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GranizadosMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> GRANIZADOS_TAB = TABS.register("granizados_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.GRANIZADO_BOMBON.get()))
                    .title(Component.translatable("itemGroup.guillotina_granizados"))
                    .displayItems((params, output) -> {
                        // Granizados
                        output.accept(ModItems.GRANIZADO_QUIPITOS.get());
                        output.accept(ModItems.GRANIZADO_REVOLCON.get());
                        output.accept(ModItems.GRANIZADO_SMINORFF_TAMARINDO.get());
                        output.accept(ModItems.GRANIZADO_BOMBON.get());
                        // Ingredientes
                        output.accept(ModItems.MEZCLA_QUIPITOS.get());
                        output.accept(ModItems.MEZCLA_REVOLCON.get());
                        output.accept(ModItems.TAMARINDO.get());
                        output.accept(ModItems.MEZCLA_BOMBON.get());
                        // Máquina
                        output.accept(new ItemStack(ModBlocks.GUILLOTINA.get()));
                    })
                    .build());
}
