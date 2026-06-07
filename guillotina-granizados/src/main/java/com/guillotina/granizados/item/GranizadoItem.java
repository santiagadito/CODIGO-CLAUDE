package com.guillotina.granizados.item;

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

    @SafeVarargs
    public GranizadoItem(Properties props, Supplier<MobEffectInstance>... effects) {
        super(props.food(new FoodProperties.Builder()
                .nutrition(4)
                .saturationMod(0.3f)
                .alwaysEat()
                .build())
                .stacksTo(16));
        this.effects = List.of(effects);
    }

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
