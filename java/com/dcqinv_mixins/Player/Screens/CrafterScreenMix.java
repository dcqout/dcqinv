package com.dcqinv_mixins.Player.Screens;

import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CrafterScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CrafterScreen.class)
public abstract class CrafterScreenMix extends AbstractContainerScreen<CrafterMenu> implements IGuiGraphics {
    public CrafterScreenMix(CrafterMenu menu, Inventory playerInventory, Component title) {super(menu, playerInventory, title);}
    @Override protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}
    private static final ResourceLocation CONTAINER_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/crafter.png");
    private static final ResourceLocation POWERED_REDSTONE_LOCATION_SPRITE = ResourceLocation.withDefaultNamespace("container/crafter/powered_redstone");
    private static final ResourceLocation UNPOWERED_REDSTONE_LOCATION_SPRITE = ResourceLocation.withDefaultNamespace("container/crafter/unpowered_redstone");

    @Overwrite
    private void renderRedstone(GuiGraphics guiGraphics) {
        int i = this.width / 2 +11;
        int j = this.height / 2 -65;
        ResourceLocation resourcelocation;
        if (((CrafterMenu)this.menu).isPowered()) {
            resourcelocation = POWERED_REDSTONE_LOCATION_SPRITE;
        } else {
            resourcelocation = UNPOWERED_REDSTONE_LOCATION_SPRITE;
        }
        guiGraphics.blitSprite(RenderType::guiTextured, resourcelocation, i, j, 16, 16);
    }

    @Override
    public void renderItemCountBg(GuiGraphics guiGraphics, int x, int y, ItemStack stack, String countString, boolean plrInv, String title) { int nm = stack.getCount();
        if (!(nm > 1)) { if (countString != null) { nm = Integer.parseInt(countString); } else { return; } }
        String s = countString == null ? String.valueOf(nm) : countString; s = s.replace("9","₉");
        s = s.replace("0","₀"); s = s.replace("1","₁"); s = s.replace("2","₂");
        s = s.replace("3","₃"); s = s.replace("4","₄"); s = s.replace("5","₅");
        s = s.replace("6","₆"); s = s.replace("7","₇"); s = s.replace("8","₈");
        boolean result = x > 130 && y < 80;
        int txtcolor = y<80?-1:-9735563;
        guiGraphics.pose().pushPose(); int xf =x+(nm>9?6:10); int yf =y+10;
        guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
        if (!result) {
            if (nm < 10)
            {guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:CONTAINER_LOCATION,x+10,yf,245.0F,4.0F,8,8,256,256);
            } else {guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:CONTAINER_LOCATION,x+6,yf,240.0F,16.0F,13,8,256,256);
            }}
        guiGraphics.drawString(font,s, x + (nm>49&&nm<60?20:20) - 2 - font.width(s), y + 8, txtcolor, false);
        guiGraphics.pose().popPose();
    }
}
