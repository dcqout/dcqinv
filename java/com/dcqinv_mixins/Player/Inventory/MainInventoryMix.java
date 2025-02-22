package com.dcqinv_mixins.Player.Inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Inventory.class)
public class MainInventoryMix {
    @Shadow public final NonNullList<ItemStack> items = NonNullList.withSize(49, ItemStack.EMPTY);

    public final Player player;
    public MainInventoryMix(Player player) {
        this.player = player;
    }
}

