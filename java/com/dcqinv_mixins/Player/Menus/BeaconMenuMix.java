package com.dcqinv_mixins.Player.Menus;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(BeaconMenu.class)
public abstract class BeaconMenuMix extends AbstractContainerMenu {
    protected BeaconMenuMix(@Nullable MenuType<?> menuType, int containerId) {super(menuType, containerId);}

    @ModifyArg(method = "<init>(ILnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",at = @At(ordinal = 0,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/BeaconMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot NewSlot(Slot arg) {
        return new Slot (arg.container, arg.getContainerSlot(),136,106) {
            @Override public boolean mayPlace(ItemStack stack) {
                return stack.is(ItemTags.BEACON_PAYMENT_ITEMS);
            }
            @Override public int getMaxStackSize() {
                    return 1;
                }
            };
        }
    @ModifyArgs(method = "<init>(ILnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",at = @At(ordinal = 0,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/BeaconMenu;addStandardInventorySlots(Lnet/minecraft/world/Container;II)V"))
    public void addInvSlots(Args args) { args.set(1,22); args.set(2,132); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 1, 51, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else {
                if (this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
                if (index >= 1 && index < 28) {
                    if (!this.moveItemStackTo(itemstack1, 28, 51, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 28 && index < 51) {
                    if (!this.moveItemStackTo(itemstack1, 1, 28, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 1, 51, false)) {
                    return ItemStack.EMPTY;
                }
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
