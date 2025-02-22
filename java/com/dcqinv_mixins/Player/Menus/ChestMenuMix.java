package com.dcqinv_mixins.Player.Menus;

import com.dcqinv.Content.PlayerGui.IContainerMenu;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ChestMenu.class)
public abstract class ChestMenuMix extends AbstractContainerMenu implements IContainerMenu {
    @Shadow private final int containerRows;

    protected ChestMenuMix(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
        containerRows = 0;
    }

    @ModifyArgs(method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;I)V",
            at= @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ChestMenu;addStandardInventorySlots(Lnet/minecraft/world/Container;II)V"))
    protected void addStandardInventorySlots(Args args) {args.set(1,8); int ys = (Integer) args.get(2);args.set(2,ys>103?ys-5:ys-7);}

    @Overwrite
    private void addChestGrid(Container container, int x, int y) { x= 30; y=9;
        for(int i = 0; i < this.containerRows; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(container, j + i * 9, (x+(j*2)) + j * 18, (y+i) + i * 18));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = (Slot)this.slots.get(index);
        ItemStack itemstack = ItemStack.EMPTY;
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.containerRows * 9) {
                if (!this.moveItemStackTo(itemstack1, this.containerRows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.containerRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

}
