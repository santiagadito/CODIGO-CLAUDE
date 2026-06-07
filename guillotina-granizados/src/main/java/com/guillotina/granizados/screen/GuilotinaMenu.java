package com.guillotina.granizados.screen;

import com.guillotina.granizados.ModMenuTypes;
import com.guillotina.granizados.blockentity.GuilotinaBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class GuilotinaMenu extends AbstractContainerMenu {

    private final Container container;
    private final ContainerData data;

    // Constructor del servidor (desde BlockEntity)
    public GuilotinaMenu(int id, Inventory playerInv, Container container, ContainerData data) {
        super(ModMenuTypes.GUILLOTINA_MENU.get(), id);
        this.container = container;
        this.data = data;

        checkContainerSize(container, 3);

        // Slot 0 – ingrediente (izquierda)
        this.addSlot(new Slot(container, 0, 56, 35));
        // Slot 1 – hielo (arriba)
        this.addSlot(new Slot(container, 1, 56, 15));
        // Slot 2 – salida (derecha)
        this.addSlot(new Slot(container, 2, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });

        // Inventario del jugador (3 filas × 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }

        addDataSlots(data);
    }

    // Constructor del cliente (desde FriendlyByteBuf)
    public GuilotinaMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, new SimpleContainer(3), new SimpleContainerData(2));
    }

    public int getProgress()    { return data.get(0); }
    public int getMaxProgress() { return data.get(1); }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            result = slotItem.copy();

            if (index < 3) {
                // Máquina → inventario
                if (!this.moveItemStackTo(slotItem, 3, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Inventario → máquina (intenta ingrediente o hielo)
                if (!this.moveItemStackTo(slotItem, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotItem.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }
}
