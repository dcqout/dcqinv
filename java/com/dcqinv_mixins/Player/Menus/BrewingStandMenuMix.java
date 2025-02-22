package com.dcqinv_mixins.Player.Menus;

import com.dcqinv.Content.PlayerGui.ISlot;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BrewingStandMenu.class)
public abstract class BrewingStandMenuMix extends AbstractContainerMenu {
    protected BrewingStandMenuMix(@Nullable MenuType<?> menuType, int containerId, Slot ingredientSlot) {super(menuType, containerId);
        this.ingredientSlot = ingredientSlot;
    } @Shadow private final Slot ingredientSlot;

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V",at = @At(ordinal = 0,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/BrewingStandMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn1(Slot arg) {
        return ((ISlot)arg).newInstance(77,46);
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V",at = @At(ordinal = 1,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/BrewingStandMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn2(Slot arg) {
        return ((ISlot)arg).newInstance(100,53);
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V",at = @At(ordinal = 2,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/BrewingStandMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn3(Slot arg) {
        return ((ISlot)arg).newInstance(123,46);
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V",at = @At(ordinal = 3,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/BrewingStandMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn0(Slot arg) {
        return ((ISlot)arg).newInstance(100,12);
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V",at = @At(ordinal = 4,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/BrewingStandMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn00(Slot arg) {
        return ((ISlot)arg).newInstance(38,12);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if ((index < 0 || index > 2) && index != 3 && index != 4) {
                if (itemstack.is(ItemTags.BREWING_FUEL)) {
                    if (this.moveItemStackTo(itemstack1, 4, 5, false)
                            || this.ingredientSlot.mayPlace(itemstack1) && !this.moveItemStackTo(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.ingredientSlot.mayPlace(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (player.level().potionBrewing().isInput(itemstack) || itemstack.is(Items.GLASS_BOTTLE)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 5 && index < 32) {
                    if (!this.moveItemStackTo(itemstack1, 32, 55, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 32 && index < 55) {
                    if (!this.moveItemStackTo(itemstack1, 5, 32, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 5, 55, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 5, 55, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            }
            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemstack);
        }
        return itemstack;
    }
}
