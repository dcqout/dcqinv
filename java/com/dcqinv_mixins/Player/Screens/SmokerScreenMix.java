package com.dcqinv_mixins.Player.Screens;

import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.SmokerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(SmokerScreen.class)
public abstract class SmokerScreenMix extends AbstractFurnaceScreen<SmokerMenu> implements IGuiGraphics {
    public SmokerScreenMix(SmokerMenu menu, Inventory playerInventory, Component title, Component recipeFilterName, ResourceLocation texture, ResourceLocation litProgressSprite, ResourceLocation burnProgressSprite, List<RecipeBookComponent.TabInfo> tabInfos) {
        super(menu, playerInventory, title, recipeFilterName, texture, litProgressSprite, burnProgressSprite, tabInfos);}
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX+2, this.titleLabelY-1, 7629682, false);
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 30, this.height / 2 - 63);
    }

    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/smoker.png");
    private static final ResourceLocation LIT_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/smoker/lit_progress");
    private static final ResourceLocation BURN_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/smoker/burn_progress");

    @Override
    protected void renderBg(GuiGraphics p_282928_, float p_281631_, int p_281252_, int p_281891_) {
        int i = this.leftPos; boolean i1;
        int j = this.topPos;
        p_282928_.blit(RenderType::guiTextured, TEXTURE, i, j, 0.0F, 0.0F, 215, 194, 256, 256);
        int j1;
        if (((AbstractFurnaceMenu)this.menu).isLit()) { i1 = true;
            j1 = Mth.ceil(((AbstractFurnaceMenu)this.menu).getLitProgress() * 13.0F) + 1;
            p_282928_.blitSprite(RenderType::guiTextured, LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - j1, i + 68, j + 36 + 15 - j1, 14, j1);
        } i1 = true;
        j1 = Mth.ceil(((AbstractFurnaceMenu)this.menu).getBurnProgress() * 24.0F);
        p_282928_.blitSprite(RenderType::guiTextured, BURN_PROGRESS_SPRITE, 24, 16, 0, 0, i + 91, j + 35, j1, 16);
    }

    @Override
    public void renderItemCountBg(GuiGraphics guiGraphics, int x, int y, ItemStack stack, String countString, boolean plrInv, String title) { int nm = stack.getCount();
        if (!(nm > 1)) { if (countString != null) { nm = Integer.parseInt(countString); } else { return; } }
        String s = countString == null ? String.valueOf(nm) : countString; s = s.replace("9","₉");
        s = s.replace("0","₀"); s = s.replace("1","₁"); s = s.replace("2","₂");
        s = s.replace("3","₃"); s = s.replace("4","₄"); s = s.replace("5","₅");
        s = s.replace("6","₆"); s = s.replace("7","₇"); s = s.replace("8","₈");
        boolean result = x > 115 && y < 59;
        int txtcolor = result?-1:-9735563;
        guiGraphics.pose().pushPose(); int xf =x+(nm>9?6:10); int yf =y+10;
        guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
        if (!result){if (nm < 10) {guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:TEXTURE,x+10,yf,245.0F,4.0F,8,8,256,256);
        } else {guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:TEXTURE,x+6,yf,240.0F,16.0F,13,8,256,256);
        }}
        guiGraphics.drawString(font,s, x + (nm>49&&nm<60?20:20) - 2 - font.width(s), y + 8, txtcolor, false);
        guiGraphics.pose().popPose();
    }

}
