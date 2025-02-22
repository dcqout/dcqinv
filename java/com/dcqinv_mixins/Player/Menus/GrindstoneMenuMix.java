package com.dcqinv_mixins.Player.Menus;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMix extends AbstractContainerMenu {
    protected GrindstoneMenuMix(@Nullable MenuType<?> menuType, int containerId) {super(menuType, containerId);repairSlots = null;
        access = null;
    }
    @Shadow final Container repairSlots; @Shadow private final ContainerLevelAccess access;

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",at = @At(ordinal = 0,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/GrindstoneMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn1(Slot arg) {return new Slot(arg.container,arg.getContainerSlot(),69,16) {
        public boolean mayPlace(ItemStack p_39607_) {
            return p_39607_.isDamageableItem() || EnchantmentHelper.hasAnyEnchantments(p_39607_);
        }
    };}

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",at = @At(ordinal = 1,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/GrindstoneMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn2(Slot arg) {return new Slot(arg.container,arg.getContainerSlot(),69,37) {
        public boolean mayPlace(ItemStack p_39616_) {
            return p_39616_.isDamageableItem() || EnchantmentHelper.hasAnyEnchantments(p_39616_);
        }
    };}

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",at = @At(ordinal = 2,value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/GrindstoneMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot Slotn3(Slot arg) {
        return new Slot(arg.container,arg.getContainerSlot(),149,31) {
        @Override public boolean mayPlace(ItemStack p_39630_) {
            return false;
        }
        @Override public void onTake(Player p_150574_, ItemStack p_150575_) {
            access.execute((p_39634_, p_39635_) -> {if (p_39634_ instanceof ServerLevel) {
                    ExperienceOrb.award((ServerLevel)p_39634_, Vec3.atCenterOf(p_39635_), this.getExperienceAmount(p_39634_));}
                p_39634_.levelEvent(1042, p_39635_, 0);});
            repairSlots.setItem(0, ItemStack.EMPTY); repairSlots.setItem(1, ItemStack.EMPTY);
        }
        private int getExperienceAmount(Level level) {
            int i = 0;
            i += this.getExperienceFromItem(repairSlots.getItem(0));
            i += this.getExperienceFromItem(repairSlots.getItem(1));
            if (i > 0) {
                int j = (int)Math.ceil((double)i / 2.0);
                return j + level.random.nextInt(j);
            } else {
                return 0;
            }
        }
        private int getExperienceFromItem(ItemStack stack) {
            int i = 0;
            ItemEnchantments itemenchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemenchantments.entrySet()) {
                Holder<Enchantment> holder = entry.getKey();
                int j = entry.getIntValue();
                if (!holder.is(EnchantmentTags.CURSE)) {
                    i += holder.value().getMinCost(j);
                }
            }
            return i;
        }
    };}

    @ModifyArgs(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/GrindstoneMenu;addStandardInventorySlots(Lnet/minecraft/world/Container;II)V"))
    protected void addStandardInventorySlots(Args args) { args.set(1,8); args.set(2,((Integer)args.get(2)));}

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            ItemStack itemstack2 = this.repairSlots.getItem(0);
            ItemStack itemstack3 = this.repairSlots.getItem(1);
            if (index == 2) {
                if (!this.moveItemStackTo(itemstack1, 3, 53, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index != 0 && index != 1) {
                if (!itemstack2.isEmpty() && !itemstack3.isEmpty()) {
                    if (index >= 3 && index < 30) {
                        if (!this.moveItemStackTo(itemstack1, 30, 53, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= 30 && index < 53 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 0, 2, false)) {
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
}
