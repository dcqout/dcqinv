package com.dcqinv_mixins.Player.Screens;

import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.GrindstoneScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GrindstoneScreen.class)
public abstract class GrindstoneScreenMix extends AbstractContainerScreen<GrindstoneMenu> implements IGuiGraphics {
    public GrindstoneScreenMix(GrindstoneMenu menu, Inventory playerInventory, Component title) {super(menu, playerInventory, title);}
    private static final ResourceLocation ERROR_SPRITE = ResourceLocation.withDefaultNamespace("container/grindstone/error");
    private static final ResourceLocation GRINDSTONE_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/grindstone.png");
    @Override protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

    @Override
    public void renderItemCountBg(GuiGraphics guiGraphics, int x, int y, ItemStack stack, String countString, boolean plrInv, String title) { int nm = stack.getCount();
        if (!(nm > 1)) { if (countString != null) { nm = Integer.parseInt(countString); } else { return; } }
        String s = countString == null ? String.valueOf(nm) : countString; s = s.replace("9","₉");
        s = s.replace("0","₀"); s = s.replace("1","₁"); s = s.replace("2","₂");
        s = s.replace("3","₃"); s = s.replace("4","₄"); s = s.replace("5","₅");
        s = s.replace("6","₆"); s = s.replace("7","₇"); s = s.replace("8","₈");
        int txtcolor = plrInv?-9735563:-1;
        guiGraphics.pose().pushPose(); int xf =x+(nm>9?6:10); int yf =y+10;
        guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
        if (nm < 10) {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:GRINDSTONE_LOCATION,x+10,yf,245.0F,4.0F,8,8,256,256);
        } else {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:GRINDSTONE_LOCATION,x+6,yf,240.0F,16.0F,13,8,256,256);
        }
        guiGraphics.drawString(font,s, x + (nm>49&&nm<60?20:20) - 2 - font.width(s), y + 8, txtcolor, !plrInv);
        guiGraphics.pose().popPose();
    }

    @Override
    protected void renderBg(GuiGraphics p_281991_, float p_282138_, int p_282937_, int p_281956_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        p_281991_.blit(RenderType::guiTextured, GRINDSTONE_LOCATION, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        if ((((GrindstoneMenu)this.menu).getSlot(0).hasItem() || ((GrindstoneMenu)this.menu).getSlot(1).hasItem()) && !((GrindstoneMenu)this.menu).getSlot(2).hasItem()) {
            p_281991_.blitSprite(RenderType::guiTextured, ERROR_SPRITE, i + 112, j + 28, 28, 21);
        }

    }

}
