package com.dcqinv_mixins.Player.Menus.Abstractions;

import com.dcqinv.Content.PlayerGui.IContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractCraftingMenu.class)
public abstract class CraftingMenuMix {

    @Shadow private final int width;
    @Shadow private final int height;
    @Shadow protected final CraftingContainer craftSlots;
    @Shadow protected final ResultContainer resultSlots;

    public CraftingMenuMix() {
        width = 0;
        height = 0;
        craftSlots = null;
        resultSlots = null;
    }

    @Overwrite
    protected Slot addResultSlot(Player player, int x, int y) {
        IContainerMenu caster = ((IContainerMenu)this);
        if (y == 28) { x = 170; y = 37; }
        return caster.addNewSlot(new ResultSlot(player, this.craftSlots, this.resultSlots, 0, x, y));
    }

    @Overwrite
    protected void addCraftingGridSlots(int x, int y) {
        IContainerMenu caster = ((IContainerMenu)this);
        if (y == 18) {x= 110; y = 27;}
        for(int i = 0; i < this.width; ++i) {
            for(int j = 0; j < this.height; ++j) {
                caster.addNewSlot(new Slot(this.craftSlots, j + i * this.width, (x+(j*2)) + j * 18, (y+i) + i * 18));
            }
        }

    }

}
