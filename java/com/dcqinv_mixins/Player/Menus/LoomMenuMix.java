package com.dcqinv_mixins.Player.Menus;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LoomMenu.class)
public abstract class LoomMenuMix extends AbstractContainerMenu {
    protected LoomMenuMix(@Nullable MenuType<?> menuType, int containerId, Slot bannerSlot, Slot dyeSlot, Slot patternSlot, Slot resultSlot, ContainerLevelAccess access, DataSlot selectedBannerPatternIndex, Slot patternSlot1, Slot resultSlot1) {super(menuType, containerId);
        this.bannerSlot = bannerSlot;
        this.dyeSlot = dyeSlot;
        this.access = access;
        this.selectedBannerPatternIndex = selectedBannerPatternIndex;
        this.patternSlot = patternSlot1;
        this.resultSlot = resultSlot1;
    }

    @Shadow private final ContainerLevelAccess access;
    @Shadow final DataSlot selectedBannerPatternIndex;
    @Shadow final Slot bannerSlot;
    @Shadow final Slot dyeSlot;
    @Shadow long lastSoundTime;
    @Shadow private final Slot patternSlot;
    @Shadow private final Slot resultSlot;

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",at = @At(ordinal = 0,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/LoomMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn1(Slot arg) {
        return new Slot(arg.container, 0, 35, 20) {
            @Override public boolean mayPlace(ItemStack stack) {return stack.getItem() instanceof BannerItem;}
        };
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",at = @At(ordinal = 1,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/LoomMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn2(Slot arg) {
        return new Slot(arg.container, 1, 55, 20) {
            @Override public boolean mayPlace(ItemStack stack) {return stack.getItem() instanceof DyeItem;}
        };
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",at = @At(ordinal = 2,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/LoomMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn3(Slot arg) {
        return new Slot(arg.container, 2, 45, 39) {
            @Override public boolean mayPlace(ItemStack stack) {return stack.getItem() instanceof BannerPatternItem;}
        };
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",at = @At(ordinal = 3,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/LoomMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn4(Slot arg) {
        return new Slot(arg.container, 0, 165, 52) { @Override public boolean mayPlace(ItemStack stack) {return false;}
            @Override
            public void onTake(Player player, ItemStack stack) {
                LoomMenuMix.this.bannerSlot.remove(1);
                LoomMenuMix.this.dyeSlot.remove(1);
                if (!LoomMenuMix.this.bannerSlot.hasItem() || !LoomMenuMix.this.dyeSlot.hasItem()) {
                    LoomMenuMix.this.selectedBannerPatternIndex.set(-1);
                }
                access.execute((p_39952_, p_39953_) -> {
                    long i = p_39952_.getGameTime();
                    if (LoomMenuMix.this.lastSoundTime != i) {
                        p_39952_.playSound(null, p_39953_, SoundEvents.UI_LOOM_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        LoomMenuMix.this.lastSoundTime = i;
                    }
                });
                super.onTake(player, stack);
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
            if (index == this.resultSlot.index) {
                if (!this.moveItemStackTo(itemstack1, 4, 54, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index != this.dyeSlot.index && index != this.bannerSlot.index && index != this.patternSlot.index) {
                if (itemstack1.getItem() instanceof BannerItem) {
                    if (!this.moveItemStackTo(itemstack1, this.bannerSlot.index, this.bannerSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.getItem() instanceof DyeItem) {
                    if (!this.moveItemStackTo(itemstack1, this.dyeSlot.index, this.dyeSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.getItem() instanceof BannerPatternItem) {
                    if (!this.moveItemStackTo(itemstack1, this.patternSlot.index, this.patternSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 4 && index < 31) {
                    if (!this.moveItemStackTo(itemstack1, 31, 54, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 31 && index < 54 && !this.moveItemStackTo(itemstack1, 4, 31, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 4, 54, false)) {
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
