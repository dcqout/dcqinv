package com.dcqinv.Content.PlayerGui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public interface IGuiGraphics {
    void renderItemCountBg(GuiGraphics guiGraphics, int x, int y, ItemStack stack, String sc, boolean inv, String title);
}
