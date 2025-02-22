package com.dcqinv.Content.PlayerGui;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

public interface ISlot {
    Player getPlr();
    Slot newInstance(int x,int y);
}
