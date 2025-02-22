package com.dcqinv_mixins.Player.Menus;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(StonecutterMenu.class)
public abstract class StonecutterMenuMix extends AbstractContainerMenu {
    protected StonecutterMenuMix(@Nullable MenuType<?> menuType, int containerId, Slot inputSlot, Slot resultSlot, ContainerLevelAccess access, DataSlot selectedRecipeIndex, ResultContainer resultContainer, Level level) {super(menuType, containerId);
        this.inputSlot = inputSlot; this.resultSlot = resultSlot; this.access = access; this.selectedRecipeIndex = selectedRecipeIndex;
        this.resultContainer = resultContainer; this.level = level;
    }
    @Shadow long lastSoundTime; @Shadow final Slot inputSlot; @Shadow final Slot resultSlot; @Shadow private final ContainerLevelAccess access;
    @Shadow final DataSlot selectedRecipeIndex; @Shadow final ResultContainer resultContainer; @Shadow private final Level level;

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",at = @At(ordinal = 0,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/StonecutterMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn1(Slot arg) {return new Slot(arg.container, 0, 36, 33);}
    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",at = @At(ordinal = 1,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/StonecutterMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn2(Slot arg) {return new Slot(arg.container, 1, 159, 33) {
        public boolean mayPlace(ItemStack p_40362_) {
            return false;
        }
        public void onTake(Player p_150672_, ItemStack p_150673_) {
            p_150673_.onCraftedBy(p_150672_.level(), p_150672_, p_150673_.getCount());
            StonecutterMenuMix.this.resultContainer.awardUsedRecipes(p_150672_, this.getRelevantItems());
            ItemStack itemstack = StonecutterMenuMix.this.inputSlot.remove(1);
            if (!itemstack.isEmpty()) {
                StonecutterMenuMix.this.setupResultSlot(StonecutterMenuMix.this.selectedRecipeIndex.get());
            }
            access.execute((p_40364_, p_40365_) -> {
                long i = p_40364_.getGameTime();
                if (StonecutterMenuMix.this.lastSoundTime != i) {
                    p_40364_.playSound((Player)null, p_40365_, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                    StonecutterMenuMix.this.lastSoundTime = i;
                }
            });
            super.onTake(p_150672_, p_150673_);
        }
        private List<ItemStack> getRelevantItems() {
            return List.of(StonecutterMenuMix.this.inputSlot.getItem());
        }
    };}

    @Shadow void setupResultSlot(int id) {}

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();
            if (index == 1) {
                item.onCraftedBy(itemstack1, player.level(), player);
                if (!this.moveItemStackTo(itemstack1, 2, 52, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 2, 52, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.level.recipeAccess().stonecutterRecipes().acceptsInput(itemstack1)) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 2 && index < 29) {
                if (!this.moveItemStackTo(itemstack1, 29, 52, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 29 && index < 52 && !this.moveItemStackTo(itemstack1, 2, 29, false)) {
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
            if (index == 1) {
                player.drop(itemstack1, false);
            }
            this.broadcastChanges();
        }
        return itemstack;
    }
}
