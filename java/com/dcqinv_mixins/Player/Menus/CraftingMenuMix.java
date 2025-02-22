package com.dcqinv_mixins.Player.Menus;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMix extends AbstractCraftingMenu {
    public CraftingMenuMix(MenuType<?> menuType, int containerId, int width, int height, ContainerLevelAccess access) {super(menuType, containerId, width, height);
        this.access = access;
    }
    @Shadow private final ContainerLevelAccess access;

    @Override
    protected Slot addResultSlot(Player player, int x, int y) {
        return this.addSlot(new ResultSlot(player, this.craftSlots, this.resultSlots, 0, 144, 35));
    }

    @Override
    protected void addCraftingGridSlots(int x, int y) { x = 51; y = 16;
        for(int i = 0; i < this.getGridWidth(); ++i) {
            for(int j = 0; j < this.getGridHeight(); ++j) {
                this.addSlot(new Slot(this.craftSlots, j + i * this.getGridWidth(), (x+(j*2)) + j * 18, (y+i) + i * 18));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 0) {
                this.access.execute((p_39378_, p_39379_) -> {
                    itemstack1.getItem().onCraftedBy(itemstack1, p_39378_, player);
                });
                if (!this.moveItemStackTo(itemstack1, 10, 60, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index >= 10 && index < 60) {
                if (!this.moveItemStackTo(itemstack1, 1, 10, false)) {
                    if (index < 50) {
                        if (!this.moveItemStackTo(itemstack1, 50, 60, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemstack1, 10, 50, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, 10, 60, false)) {
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
            if (index == 0) {
                player.drop(itemstack1, false);
            }
        }
        return itemstack;
    }
}
