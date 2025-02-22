package com.dcqinv_mixins.Player.Inventory.Slots;

import com.dcqinv.Content.PlayerGui.ISlot;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$IngredientsSlot")
public class IngredientsSlotMix extends Slot implements ISlot {
    @Shadow private final PotionBrewing potionBrewing;

    public IngredientsSlotMix(Container container, int slot, int x, int y) {super(container, slot, x, y);potionBrewing = null;}

    public class IngredientsSlot extends Slot {
        private final PotionBrewing potionBrewing;

        public IngredientsSlot(PotionBrewing potionBrewing, Container container, int slot, int x, int y) {
            super(container, slot, x, y);
            this.potionBrewing = potionBrewing;
        }

        public boolean mayPlace(ItemStack stack) {
            return this.potionBrewing.isIngredient(stack);
        }
    }

    @Override
    public Player getPlr() {
        return null;
    }

    @Override
    public Slot newInstance(int x, int y) {
        return new IngredientsSlot(this.potionBrewing,this.container,this.index,x,y);
    }
}

