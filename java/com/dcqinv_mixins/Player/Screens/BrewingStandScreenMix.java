package com.dcqinv_mixins.Player.Screens;

import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BrewingStandScreen.class)
public abstract class BrewingStandScreenMix extends AbstractContainerScreen<BrewingStandMenu> implements IGuiGraphics {
    public BrewingStandScreenMix(BrewingStandMenu menu, Inventory playerInventory, Component title) {super(menu, playerInventory, title);}
    @Override protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

    private static final ResourceLocation BREWING_STAND_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/brewing_stand.png");
    private static final ResourceLocation FUEL_LENGTH_SPRITE = ResourceLocation.withDefaultNamespace("container/brewing_stand/fuel_length");
    private static final ResourceLocation BREW_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/brewing_stand/brew_progress");
    private static final ResourceLocation BUBBLES_SPRITE = ResourceLocation.withDefaultNamespace("container/brewing_stand/bubbles");
    private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};

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
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:BREWING_STAND_LOCATION,x+10,yf,245.0F,4.0F,8,8,256,256);
            } else {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:BREWING_STAND_LOCATION,x+6,yf,240.0F,16.0F,13,8,256,256);
            }
        guiGraphics.drawString(font,s, x + (nm>49&&nm<60?20:20) - 2 - font.width(s), y + 8, txtcolor, false);
        guiGraphics.pose().popPose();
    }

    protected void renderBg(GuiGraphics p_282963_, float p_282080_, int p_283365_, int p_283150_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        p_282963_.blit(RenderType::guiTextured, BREWING_STAND_LOCATION, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        int k = ((BrewingStandMenu)this.menu).getFuel();
        int l = Mth.clamp((18 * k + 20 - 1) / 20, 0, 18);
        if (l > 0) {
            p_282963_.blitSprite(RenderType::guiTextured, FUEL_LENGTH_SPRITE, 18, 4, 0, 0, i + 81, j + 39, l, 4);
        }

        int i1 = ((BrewingStandMenu)this.menu).getBrewingTicks();
        if (i1 > 0) {
            int j1 = (int)(28.0F * (1.0F - (float)i1 / 400.0F));
            if (j1 > 0) {
                p_282963_.blitSprite(RenderType::guiTextured, BREW_PROGRESS_SPRITE, 9, 28, 0, 0, i + 97+21, j + 16 - 5, 9, j1);
            }

            j1 = BUBBLELENGTHS[i1 / 2 % 7];
            if (j1 > 0) {
                p_282963_.blitSprite(RenderType::guiTextured, BUBBLES_SPRITE, 12, 29, 0, 29 - j1, i + 63+21, j + 14 - 5 + 29 - j1, 12, j1);
            }
        }

    }
}
