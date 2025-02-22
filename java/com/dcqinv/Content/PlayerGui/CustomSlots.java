package com.dcqinv.Content.PlayerGui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;

public class CustomSlots {

    public static class CustomCreativeSlot extends Slot {
        public CustomCreativeSlot(Container container, int id, int x, int y) {
            super(container, id, x, y);
        }
        public boolean mayPickup(Player player) {
            ItemStack itemstack = this.getItem();
            return super.mayPickup(player) && !itemstack.isEmpty() ? itemstack.isItemEnabled(player.level().enabledFeatures()) && !itemstack.has(DataComponents.CREATIVE_SLOT_LOCK) : itemstack.isEmpty();
        }
    }

    public class ArmorSlot extends Slot {
        private final LivingEntity owner;
        private final EquipmentSlot slot;
        @Nullable
        private final ResourceLocation emptyIcon;

        public ArmorSlot(Container container, LivingEntity owner, EquipmentSlot slot, int slotIndex, int x, int y, @Nullable ResourceLocation emptyIcon) {
            super(container, slotIndex, x, y);
            this.owner = owner;
            this.slot = slot;
            this.emptyIcon = emptyIcon;
        }

        public void setByPlayer(ItemStack stack1, ItemStack stack2) {
            this.owner.onEquipItem(this.slot, stack2, stack1);
            super.setByPlayer(stack1, stack2);
        }

        public int getMaxStackSize() {
            return 1;
        }

        public boolean mayPlace(ItemStack stack) {
            return this.owner.getEquipmentSlotForItem(stack) == this.slot;
        }

        public boolean mayPickup(Player player) {
            ItemStack itemstack = this.getItem();
            return !itemstack.isEmpty() && !player.isCreative() && EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) ? false : super.mayPickup(player);
        }

        @Nullable
        public ResourceLocation getNoItemIcon() {
            return this.emptyIcon;
        }
    }

    public class SlotWrap extends Slot {
        public final Slot target;

        public SlotWrap(Slot slot, int index, int x, int y) {
            super(slot.container, index, x, y);
            this.target = slot;
        }

        @Override
        public void onTake(Player p_169754_, ItemStack p_169755_) {
            this.target.onTake(p_169754_, p_169755_);
        }

        /**
         * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
         */
        @Override
        public boolean mayPlace(ItemStack stack) {
            return this.target.mayPlace(stack);
        }

        @Override
        public ItemStack getItem() {
            return this.target.getItem();
        }

        @Override
        public boolean hasItem() {
            return this.target.hasItem();
        }

        @Override
        public void setByPlayer(ItemStack p_271008_, ItemStack p_299868_) {
            this.target.setByPlayer(p_271008_, p_299868_);
        }

        /**
         * Helper method to put a stack in the slot.
         */
        @Override
        public void set(ItemStack stack) {
            this.target.set(stack);
        }

        @Override
        public void setChanged() {
            this.target.setChanged();
        }

        @Override
        public int getMaxStackSize() {
            return this.target.getMaxStackSize();
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return this.target.getMaxStackSize(stack);
        }

        @Nullable
        @Override
        public ResourceLocation getNoItemIcon() {
            return this.target.getNoItemIcon();
        }

        /**
         * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new stack.
         */
        @Override
        public ItemStack remove(int amount) {
            return this.target.remove(amount);
        }

        @Override
        public boolean isActive() {
            return this.target.isActive();
        }

        /**
         * Return whether this slot's stack can be taken from this slot.
         */
        @Override
        public boolean mayPickup(Player player) {
            return this.target.mayPickup(player);
        }

        public int getSlotIndex() {
            return this.target.index;
        }

        public boolean isSameInventory(Slot other) {
            return this.container == other.container;
        }
    }
}

