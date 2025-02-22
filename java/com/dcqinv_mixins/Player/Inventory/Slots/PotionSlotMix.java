package com.dcqinv_mixins.Player.Inventory.Slots;

import com.dcqinv.Content.PlayerGui.ISlot;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot")
public class PotionSlotMix extends Slot implements ISlot {

    public PotionSlotMix(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    public class PotionSlot extends Slot {
        static final ResourceLocation EMPTY_SLOT_POTION = ResourceLocation.withDefaultNamespace("container/slot/potion");

        public PotionSlot(Container p_39123_, int p_39124_, int p_39125_, int p_39126_) {
            super(p_39123_, p_39124_, p_39125_, p_39126_);
        }

        public boolean mayPlace(ItemStack stack) {
            return mayPlaceItem(stack);
        }

        public int getMaxStackSize() {
            return 1;
        }

        public void onTake(Player p_150499_, ItemStack p_150500_) {
            Optional<Holder<Potion>> optional = ((PotionContents)p_150500_.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).potion();
            if (optional.isPresent() && p_150499_ instanceof ServerPlayer serverplayer) {
                CriteriaTriggers.BREWED_POTION.trigger(serverplayer, (Holder)optional.get());
            }
            super.onTake(p_150499_, p_150500_);
        }

        /** @deprecated */
        @Deprecated
        public static boolean mayPlaceItem(ItemStack stack) {
            return stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION) || stack.is(Items.GLASS_BOTTLE);
        }

        public static boolean mayPlaceItem(PotionBrewing potionBrewing, ItemStack p_39134_) {
            return mayPlaceItem(p_39134_);//potionBrewing.isInput(p_39134_) || p_39134_.is(Items.GLASS_BOTTLE);
        }

        public ResourceLocation getNoItemIcon() {
            return EMPTY_SLOT_POTION;
        }
    }

    @Override
    public Slot newInstance(int x, int y) {
        return new PotionSlot(this.container,this.index,x,y);
    }

    @Override
    public Player getPlr() {
        return null;
    }

}
