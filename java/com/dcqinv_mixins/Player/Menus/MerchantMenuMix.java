package com.dcqinv_mixins.Player.Menus;

import com.dcqinv.Content.PlayerGui.ISlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(MerchantMenu.class)
public abstract class MerchantMenuMix extends AbstractContainerMenu {
    @Shadow public MerchantOffers getOffers() {return null;}
    protected MerchantMenuMix(@Nullable MenuType<?> menuType, int containerId, Merchant trader, MerchantContainer tradeContainer) {super(menuType, containerId);
        this.trader = trader;this.tradeContainer = tradeContainer;
    } @Shadow private void playTradeSound() {}
    @Shadow private final Merchant trader; @Shadow private final MerchantContainer tradeContainer;
    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/trading/Merchant;)V",at = @At(ordinal = 0,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/MerchantMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn1(Slot arg) {return new Slot(arg.container, 0, 9, 17);}
    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/trading/Merchant;)V",at = @At(ordinal = 1,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/MerchantMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn2(Slot arg) {return new Slot(arg.container, 1, 35, 17);}
    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/trading/Merchant;)V",at = @At(ordinal = 2,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/MerchantMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn3(Slot arg) {return new MerchantResultSlot(((ISlot)arg).getPlr(),this.trader,this.tradeContainer, 2, 93, 17);}
    @ModifyArgs(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/trading/Merchant;)V",at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/MerchantMenu;addStandardInventorySlots(Lnet/minecraft/world/Container;II)V"))
    public void InvSlots(Args args) {args.set(1,135);args.set(2,92);}

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 2) {
                if (!this.moveItemStackTo(itemstack1, 3, 53, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
                this.playTradeSound();
            } else if (index != 0 && index != 1) {
                if (index >= 3 && index < 30) {
                    if (!this.moveItemStackTo(itemstack1, 30, 53, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 53 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 53, false)) {
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

    @Overwrite
    private void moveFromInventoryToPaymentSlot(int paymentSlotIndex, ItemCost payment) {
        for(int i = 3; i < 53; ++i) {
            ItemStack itemstack = ((Slot)this.slots.get(i)).getItem();
            if (!itemstack.isEmpty() && payment.test(itemstack)) {
                ItemStack itemstack1 = this.tradeContainer.getItem(paymentSlotIndex);
                if (itemstack1.isEmpty() || ItemStack.isSameItemSameComponents(itemstack, itemstack1)) {
                    int j = itemstack.getMaxStackSize();
                    int k = Math.min(j - itemstack1.getCount(), itemstack.getCount());
                    ItemStack itemstack2 = itemstack.copyWithCount(itemstack1.getCount() + k);
                    itemstack.shrink(k);
                    this.tradeContainer.setItem(paymentSlotIndex, itemstack2);
                    if (itemstack2.getCount() >= j) {
                        break;
                    }
                }
            }
        }

    }

    @Overwrite
    public void tryMoveItems(int selectedMerchantRecipe) {
        if (selectedMerchantRecipe >= 0 && this.getOffers().size() > selectedMerchantRecipe) {
            ItemStack itemstack = this.tradeContainer.getItem(0);
            if (!itemstack.isEmpty()) {
                if (!this.moveItemStackTo(itemstack, 3, 53, true)) {
                    return;
                }
                this.tradeContainer.setItem(0, itemstack);
            }
            ItemStack itemstack1 = this.tradeContainer.getItem(1);
            if (!itemstack1.isEmpty()) {
                if (!this.moveItemStackTo(itemstack1, 3, 53, true)) {
                    return;
                }
                this.tradeContainer.setItem(1, itemstack1);
            }
            if (this.tradeContainer.getItem(0).isEmpty() && this.tradeContainer.getItem(1).isEmpty()) {
                MerchantOffer merchantoffer = (MerchantOffer)this.getOffers().get(selectedMerchantRecipe);
                this.moveFromInventoryToPaymentSlot(0, merchantoffer.getItemCostA());
                merchantoffer.getItemCostB().ifPresent((p_330236_) -> {
                    this.moveFromInventoryToPaymentSlot(1, p_330236_);
                });
            }
        }

    }
}
