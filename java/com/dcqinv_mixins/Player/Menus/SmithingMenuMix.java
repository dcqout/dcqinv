package com.dcqinv_mixins.Player.Menus;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipePropertySet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Objects;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMix extends ItemCombinerMenu {
    public SmithingMenuMix(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition) {super(menuType, containerId, inventory, access, slotDefinition);}

    @Overwrite
    private static ItemCombinerMenuSlotDefinition createInputSlotDefinitions(RecipeAccess access) {
        RecipePropertySet recipepropertyset = access.propertySet(RecipePropertySet.SMITHING_BASE);
        RecipePropertySet recipepropertyset1 = access.propertySet(RecipePropertySet.SMITHING_TEMPLATE);
        RecipePropertySet recipepropertyset2 = access.propertySet(RecipePropertySet.SMITHING_ADDITION);
        ItemCombinerMenuSlotDefinition.Builder var10000 = ItemCombinerMenuSlotDefinition.create();
        Objects.requireNonNull(recipepropertyset1);
        var10000 = var10000.withSlot(0, 32, 48, recipepropertyset1::test);
        Objects.requireNonNull(recipepropertyset);
        var10000 = var10000.withSlot(1, 52, 48, recipepropertyset::test);
        Objects.requireNonNull(recipepropertyset2);
        return var10000.withSlot(2, 72, 48, recipepropertyset2::test).withResultSlot(3, 121, 48).build();
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
