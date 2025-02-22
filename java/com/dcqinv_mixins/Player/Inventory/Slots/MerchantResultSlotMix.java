package com.dcqinv_mixins.Player.Inventory.Slots;

import com.dcqinv.Content.PlayerGui.ISlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MerchantResultSlot.class)
public class MerchantResultSlotMix implements ISlot {
    public MerchantResultSlotMix(Player player) {this.player = player;}
    @Shadow private final Player player;
    @Override
    public Player getPlr() {
        return player;
    }
    @Override
    public Slot newInstance(int x, int y) {
        return null;
    }
}
