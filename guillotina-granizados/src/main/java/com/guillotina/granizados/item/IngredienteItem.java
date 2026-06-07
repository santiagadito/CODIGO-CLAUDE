package com.guillotina.granizados.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class IngredienteItem extends Item {

    private final int nameColor;

    public IngredienteItem(Properties props, int nameColor) {
        super(props);
        this.nameColor = nameColor;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId())
                .withStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(TextColor.fromRgb(nameColor))
                        .withItalic(false));
    }
}
