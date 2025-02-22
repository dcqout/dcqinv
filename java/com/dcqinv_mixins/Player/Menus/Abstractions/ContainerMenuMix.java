package com.dcqinv_mixins.Player.Menus.Abstractions;

import com.dcqinv.Content.PlayerGui.IContainerMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractContainerMenu.class)
public abstract class ContainerMenuMix implements IContainerMenu {

    @Shadow public final NonNullList<Slot> slots;
    @Shadow private final NonNullList<ItemStack> lastSlots;
    @Shadow private final NonNullList<ItemStack> remoteSlots;

    public ContainerMenuMix() {
        slots = null;
        lastSlots = null;
        remoteSlots = null;
    }

    @Overwrite protected Slot addSlot(Slot slot) {
        if (slot.getSlotIndex() == 500) { return slot; }
        slot.index = this.slots.size();
        this.slots.add(slot);
        this.lastSlots.add(ItemStack.EMPTY);
        this.remoteSlots.add(ItemStack.EMPTY);
        return slot;
    }

    @Overwrite
    protected void addInventoryHotbarSlots(Container container, int x, int y) {
        for(int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(container, i,(x+(i*2)) + i * 18, y));
        }
    }

    @Overwrite
    protected void addInventoryExtendedSlots(Container container, int x, int y) {
        int z = 18; this.addSlot(new Slot(container, 9, x,y)); x = x+2;
        for (int r = 1; r < 10; ++r) { this.addSlot(new Slot(container,r+9,(x+(r*2))+r*18,y));}
        y = y+22; for(int i = 0; i < 3; ++i) {for(int j = 0; j < 10; ++j) {
                z = z+1; this.addSlot(new Slot(container,z,(j>0?(x+(j*2)):x-2)+j*18,(y+i)+i*18));
            }
        }
        if (container instanceof Inventory) {
            Player plr = ((Inventory) container).player;
            Slot slot = new Slot(container, 53, x-2, y + 60) {
                @Override
                public void setByPlayer(ItemStack stack1, ItemStack stack2) {
                    plr.onEquipItem(EquipmentSlot.OFFHAND, stack2, stack1);
                    super.setByPlayer(stack1, stack2);
                }
                @Override
                public ResourceLocation getNoItemIcon() {
                    return InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD;
                }
            };
            this.addSlot(slot);
        }
    }

    @Overwrite
    protected void addStandardInventorySlots(Container container, int x, int y) {
        this.addInventoryExtendedSlots(container,x,y+3);
        int i = 4;
        int j = 58;
        this.addInventoryHotbarSlots(container, x+22, y+ 22 + 63);
    }


    @Override
    public Slot addNewSlot(Slot slot) {
        return this.addSlot(slot);
    }

    @Override
    public Player getPlr() {
        return null;
    }
}
