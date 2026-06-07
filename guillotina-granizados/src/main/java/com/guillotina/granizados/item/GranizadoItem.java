package com.guillotina.granizados.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

public class GranizadoItem extends Item {

    private final List<Supplier<MobEffectInstance>> effects;
    private final int nameColor;

    @SafeVarargs
    public GranizadoItem(Properties props, int nameColor, Supplier<MobEffectInstance>... effects) {
        super(props.food(new FoodProperties.Builder()
                .nutrition(4)
                .saturationMod(0.3f)
                .alwaysEat()
                .build())
                .stacksTo(16));
        this.effects   = List.of(effects);
        this.nameColor = nameColor;
    }

    // ── Efecto de encantamiento (brillo) ──────────────────────────────────
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    // ── Nombre en negrilla con color personalizado ────────────────────────
    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId())
                .withStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(TextColor.fromRgb(nameColor))
                        .withItalic(false));
    }

    // ── Consumir ──────────────────────────────────────────────────────────
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide() && entity instanceof Player player) {
            for (Supplier<MobEffectInstance> supplier : effects) {
                player.addEffect(supplier.get());
            }
        }
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }
}
