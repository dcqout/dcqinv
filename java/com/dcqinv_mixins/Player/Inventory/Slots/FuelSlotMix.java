package com.dcqinv_mixins.Player.Inventory.Slots;

import com.dcqinv.Content.PlayerGui.ISlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$FuelSlot")
public class FuelSlotMix extends Slot implements ISlot {
    public FuelSlotMix(Container container, int slot, int x, int y) {super(container, slot, x, y);}

    public class FuelSlot extends Slot {
        public FuelSlot(Container p_39105_, int p_39106_, int p_39107_, int p_39108_) {
            super(p_39105_, p_39106_, p_39107_, p_39108_);
        }
        static final ResourceLocation EMPTY_SLOT_FUEL = ResourceLocation.withDefaultNamespace("container/slot/brewing_fuel");

        public boolean mayPlace(ItemStack stack) {
            return mayPlaceItem(stack);
        }

        public static boolean mayPlaceItem(ItemStack itemStack) {
            return itemStack.is(ItemTags.BREWING_FUEL);
        }

        public ResourceLocation getNoItemIcon() {
            return EMPTY_SLOT_FUEL;
        }
    }


    @Override
    public Player getPlr() {
        return null;
    }

    @Override
    public Slot newInstance(int x, int y) {
        return new FuelSlot(this.container,this.getSlotIndex(),x,y);
    }
}
