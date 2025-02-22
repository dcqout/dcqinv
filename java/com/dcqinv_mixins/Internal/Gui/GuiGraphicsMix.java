package com.dcqinv_mixins.Internal.Gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMix {
    @Overwrite private void renderItemCount(Font font, ItemStack stack, int x, int y, @Nullable String text) {}
}
