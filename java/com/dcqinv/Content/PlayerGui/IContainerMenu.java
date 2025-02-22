package com.dcqinv.Content.PlayerGui;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

public interface IContainerMenu {
    Slot addNewSlot(Slot slot);
    Player getPlr();
}
