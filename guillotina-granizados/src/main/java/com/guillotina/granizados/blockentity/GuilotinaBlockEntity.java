package com.guillotina.granizados.blockentity;

import com.guillotina.granizados.ModBlockEntities;
import com.guillotina.granizados.ModItems;
import com.guillotina.granizados.screen.GuilotinaMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Map;

public class GuilotinaBlockEntity extends BlockEntity implements MenuProvider {

    // Slot 0 = ingrediente, Slot 1 = hielo, Slot 2 = salida
    private final SimpleContainer inventory = new SimpleContainer(3) {
        @Override
        public void setChanged() {
            super.setChanged();
            GuilotinaBlockEntity.this.setChanged();
        }
    };

    public static final int MAX_PROGRESS = 100; // 5 segundos (20 ticks/seg)
    private int progress = 0;

    private final ContainerData data = new ContainerData() {
        @Override public int get(int i)          { return i == 0 ? progress : MAX_PROGRESS; }
        @Override public void set(int i, int v)  { if (i == 0) progress = v; }
        @Override public int getCount()          { return 2; }
    };

    // ── Recetas: ingrediente → granizado ──────────────────────────────────
    private static Map<Item, Item> getRecipes() {
        return Map.of(
                ModItems.MEZCLA_QUIPITOS.get(),  ModItems.GRANIZADO_QUIPITOS.get(),
                ModItems.MEZCLA_REVOLCON.get(),  ModItems.GRANIZADO_REVOLCON.get(),
                ModItems.TAMARINDO.get(),         ModItems.GRANIZADO_SMINORFF_TAMARINDO.get(),
                ModItems.MEZCLA_BOMBON.get(),    ModItems.GRANIZADO_BOMBON.get()
        );
    }

    public GuilotinaBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GUILLOTINA_BE.get(), pos, state);
    }

    // ── Tick (solo servidor) ──────────────────────────────────────────────

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                   GuilotinaBlockEntity be) {
        if (be.hasValidRecipe()) {
            be.progress++;
            if (be.progress >= MAX_PROGRESS) {
                be.craft();
                be.progress = 0;
            }
            be.setChanged();
        } else {
            if (be.progress != 0) {
                be.progress = 0;
                be.setChanged();
            }
        }
    }

    private boolean hasValidRecipe() {
        ItemStack ingredient = inventory.getItem(0);
        ItemStack ice        = inventory.getItem(1);
        ItemStack output     = inventory.getItem(2);

        if (ingredient.isEmpty() || !isIce(ice.getItem())) return false;

        Item result = getRecipes().get(ingredient.getItem());
        if (result == null) return false;

        return output.isEmpty()
                || (output.is(result) && output.getCount() < output.getMaxStackSize());
    }

    private static boolean isIce(Item item) {
        return item == Items.ICE || item == Items.PACKED_ICE || item == Items.BLUE_ICE;
    }

    private void craft() {
        Item resultItem = getRecipes().get(inventory.getItem(0).getItem());
        ItemStack output = inventory.getItem(2);

        if (output.isEmpty()) {
            inventory.setItem(2, new ItemStack(resultItem, 1));
        } else {
            output.grow(1);
        }

        inventory.getItem(0).shrink(1);
        inventory.getItem(1).shrink(1);
    }

    // ── NBT ───────────────────────────────────────────────────────────────

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Progress", progress);
        for (int i = 0; i < 3; i++) {
            CompoundTag slotTag = new CompoundTag();
            inventory.getItem(i).save(slotTag);
            tag.put("Slot" + i, slotTag);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getInt("Progress");
        for (int i = 0; i < 3; i++) {
            inventory.setItem(i, ItemStack.of(tag.getCompound("Slot" + i)));
        }
    }

    // ── MenuProvider ──────────────────────────────────────────────────────

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.guillotina_granizados.guillotina");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new GuilotinaMenu(id, playerInv, inventory, data);
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    public ContainerData getContainerData() {
        return data;
    }
}
