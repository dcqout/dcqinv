package com.dcqinv_mixins.Player.Inventory;

import com.dcqinv.Content.PlayerGui.CustomSlots;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static net.minecraft.world.inventory.InventoryMenu.*;

@Mixin(InventoryMenu.class) @SuppressWarnings("UnreachableCode")
public abstract class InventoryMenuMix extends AbstractCraftingMenu {
    public InventoryMenuMix(MenuType<?> menuType, int containerId, int width, int height) {super(menuType, containerId, width, height);} protected int slots = 0;
    @Overwrite public static boolean isHotbarSlot(int index) {return index >= 49 && index < 58 || index == 58;}

    @ModifyArg(method = "<init>(Lnet/minecraft/world/entity/player/Inventory;ZLnet/minecraft/world/entity/player/Player;)V",at=
    @At(ordinal = 0, value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/InventoryMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    private Slot slotcheck(Slot slot) {
        Player plr = ((Inventory) slot.container).player;
        if (slots == 0) { slot = new CustomSlots().new ArmorSlot(slot.container,plr,EquipmentSlot.HEAD,52,8,8,EMPTY_ARMOR_SLOT_HELMET);
        }else if (slots == 1) { slot = new CustomSlots().new ArmorSlot(slot.container,plr,EquipmentSlot.CHEST,51,8,8+1+(slots*18),EMPTY_ARMOR_SLOT_CHESTPLATE);
        } else if (slots == 2) { slot = new CustomSlots().new ArmorSlot(slot.container,plr,EquipmentSlot.LEGS,50,8,8+2+(slots*18),EMPTY_ARMOR_SLOT_LEGGINGS);
        } else if (slots == 3) { slot = new CustomSlots().new ArmorSlot(slot.container,plr,EquipmentSlot.FEET,49,8,8+3+(slots*18),EMPTY_ARMOR_SLOT_BOOTS);
        } slots += 1; return slot;
    }

    @ModifyArg(method = "<init>(Lnet/minecraft/world/entity/player/Inventory;ZLnet/minecraft/world/entity/player/Player;)V",
            at= @At(ordinal = 1, value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/InventoryMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    private Slot buildOffHand(Slot slot) {return new Slot(slot.container,500,slot.x,slot.y);}


    @Overwrite
    public ItemStack quickMoveStack(Player player, int index) {
        InventoryMenu caster = (InventoryMenu) (Object) this;
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)caster.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            EquipmentSlot equipmentslot = player.getEquipmentSlotForItem(itemstack);
            if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 9, 58, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index >= 1 && index < 5) {
                if (!this.moveItemStackTo(itemstack1, 9, 58, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 5 && index < 9) {
                if (!this.moveItemStackTo(itemstack1, 9, 58, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (equipmentslot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && !((Slot)caster.slots.get(8 - equipmentslot.getIndex())).hasItem()) {
                int i = 8 - equipmentslot.getIndex();
                if (!this.moveItemStackTo(itemstack1, i, i + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (equipmentslot == EquipmentSlot.OFFHAND && !((Slot)caster.slots.get(58)).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 58, 59, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 9 && index < 49) {
                if (!this.moveItemStackTo(itemstack1, 49, 59, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 49 && index < 58) {
                if (!this.moveItemStackTo(itemstack1, 9, 49, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 9, 58, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY, itemstack);
            } else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemstack1);
            if (index == 0) {
                player.drop(itemstack1, false);
            }
        }
        return itemstack;
    }

}
