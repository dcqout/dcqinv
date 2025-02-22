package com.dcqinv_mixins.Player.Menus;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CartographyTableMenu.class)
public abstract class CartographyTableMenuMix extends AbstractContainerMenu {
    protected CartographyTableMenuMix(@Nullable MenuType<?> menuType, int containerId, final ContainerLevelAccess access) {super(menuType, containerId);this.access = access;}
    @Shadow private final ContainerLevelAccess access; @Shadow long lastSoundTime;

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",at = @At(ordinal = 0,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/CartographyTableMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn1(Slot arg) {
        return new Slot(arg.container, 0, 33, 14) {
            public boolean mayPlace(ItemStack p_39194_) {
                return p_39194_.has(DataComponents.MAP_ID);
            }
        };
    }
    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",at = @At(ordinal = 1,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/CartographyTableMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn2(Slot arg) {
        return new Slot(arg.container, 1, 33, 51) {
            @Override
            public boolean mayPlace(ItemStack p_39203_) {
                return p_39203_.is(Items.PAPER) || p_39203_.is(Items.MAP) || p_39203_.is(Items.GLASS_PANE);
            }
        };
    }
    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",at = @At(ordinal = 2,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/CartographyTableMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn3(Slot arg) {
        return new Slot(arg.container, 2, 162, 38) {
            @Override
            public boolean mayPlace(ItemStack p_39217_) {
                return false;
            }
            @Override
            public void onTake(Player p_150509_, ItemStack p_150510_) {
                CartographyTableMenuMix.this.slots.get(0).remove(1);
                CartographyTableMenuMix.this.slots.get(1).remove(1);
                p_150510_.getItem().onCraftedBy(p_150510_, p_150509_.level(), p_150509_);
                access.execute((p_39219_, p_39220_) -> {
                    long i = p_39219_.getGameTime();
                    if (CartographyTableMenuMix.this.lastSoundTime != i) {
                        p_39219_.playSound(null, p_39220_, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        CartographyTableMenuMix.this.lastSoundTime = i;
                    }
                });
                super.onTake(p_150509_, p_150510_);
            }
        };
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 2) {
                itemstack1.getItem().onCraftedBy(itemstack1, player.level(), player);
                if (!this.moveItemStackTo(itemstack1, 3, 53, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index != 1 && index != 0) {
                if (itemstack1.has(DataComponents.MAP_ID)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!itemstack1.is(Items.PAPER) && !itemstack1.is(Items.MAP) && !itemstack1.is(Items.GLASS_PANE)) {
                    if (index >= 3 && index < 30) {
                        if (!this.moveItemStackTo(itemstack1, 30, 53, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= 30 && index < 53 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 53, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }
            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemstack1);
            this.broadcastChanges();
        }
        return itemstack;
    }

}
