package com.dcqinv_mixins.Player.Inventory.Slots;

import com.dcqinv.Content.PlayerGui.ISlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FurnaceResultSlot.class)
public abstract class FurnaceSlotMix implements ISlot {
    @Shadow private final Player player;

    protected FurnaceSlotMix() {player = null;}

    @Override
    public Player getPlr() {
        return player;
    }
}
