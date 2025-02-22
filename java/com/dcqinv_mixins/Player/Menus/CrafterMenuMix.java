package com.dcqinv_mixins.Player.Menus;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CrafterMenu.class) @SuppressWarnings("UnreachableCode")
public abstract class CrafterMenuMix extends AbstractContainerMenu {
    protected CrafterMenuMix(@Nullable MenuType<?> menuType, int containerId) {super(menuType, containerId);container = null;resultContainer = null;
        containerData = null;
    } @Shadow private final CraftingContainer container; @Shadow private final ResultContainer resultContainer;@Shadow private final ContainerData containerData;
    @Shadow private void refreshRecipeResult() {}

    @Overwrite
    private void addSlots(Inventory playerInventory) {
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                int k = j + i * 3;
                this.addSlot(new CrafterSlot(this.container, k,(44+(j*2))+j*18,(14+i)+i*18,(CrafterMenu)(Object)this));
            }
        }
        this.addStandardInventorySlots(playerInventory, 8, 84);
        this.addSlot(new NonInteractiveResultSlot(this.resultContainer, 0, 152, 32));
        this.addDataSlots(this.containerData);
        this.refreshRecipeResult();
    }

    @Override
    public ItemStack quickMoveStack(Player p_307459_, int p_307204_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(p_307204_);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (p_307204_ < 9) {
                if (!this.moveItemStackTo(itemstack1, 9, 59, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(p_307459_, itemstack1);
        }
        return itemstack;
    }

}
