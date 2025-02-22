package com.dcqinv_mixins.Player.Menus;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMix extends ItemCombinerMenu {

    public AnvilMenuMix(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition) {
        super(menuType, containerId, inventory, access, slotDefinition);
    }

    @Overwrite
    private static ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create().withSlot(0, 46, 46, (p_266635_) -> {
            return true;
        }).withSlot(1, 95, 46, (p_266634_) -> {
            return true;
        }).withResultSlot(2, 153, 46).build();
    }

    private int getInventorySlotStart() {return this.getResultSlot() + 1;}
    private int getInventorySlotEnd() {return this.getInventorySlotStart() + 41;}
    private int getUseRowStart() {return this.getInventorySlotEnd();}
    private int getUseRowEnd() {return this.getUseRowStart() + 9;}

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            int i = this.getInventorySlotStart();
            int j = this.getUseRowEnd();
            if (index == this.getResultSlot()) {
                if (!this.moveItemStackTo(itemstack1, i, j, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index >= 0 && index < this.getResultSlot()) {
                if (!this.moveItemStackTo(itemstack1, i, j, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.canMoveIntoInputSlots(itemstack1) && index >= this.getInventorySlotStart() && index < this.getUseRowEnd()) {
                if (!this.moveItemStackTo(itemstack1, 0, this.getResultSlot(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= this.getInventorySlotStart() && index < this.getInventorySlotEnd()) {
                if (!this.moveItemStackTo(itemstack1, this.getUseRowStart(), this.getUseRowEnd(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= this.getUseRowStart() && index < this.getUseRowEnd() && !this.moveItemStackTo(itemstack1, this.getInventorySlotStart(), this.getInventorySlotEnd(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemstack1);
        }
        return itemstack;
    }

}
