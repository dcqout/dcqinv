package com.dcqinv_mixins.Player.Screens;

import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingScreen.class)
public abstract class CraftingScreenMix extends AbstractRecipeBookScreen<CraftingMenu> implements IGuiGraphics {
    public CraftingScreenMix(CraftingMenu menu, RecipeBookComponent<?> recipeBookComponent, Inventory playerInventory, Component title)
    {super(menu, recipeBookComponent, playerInventory, title);}

    private static final ResourceLocation CRAFTING_TABLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/crafting_table.png");

    @Inject(method = "<init>", at = @At("TAIL"))
    public void sp(CraftingMenu menu, Inventory playerInventory, Component title, CallbackInfo info) {
        this.imageWidth = 215; this.imageHeight = 194;
    }

    @Override
    public ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 15, this.height / 2 - 63);
    }

    @Override
    public void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX+30, this.titleLabelY-1, 7629682, false);
    }

    @Override
    public void renderItemCountBg(GuiGraphics guiGraphics, int x, int y, ItemStack stack, String countString, boolean plrInv, String title) { int nm = stack.getCount();
        if (!(nm > 1)) { if (countString != null) { nm = Integer.parseInt(countString); } else { return; } }
        String s = countString == null ? String.valueOf(nm) : countString; s = s.replace("9","₉");
        s = s.replace("0","₀"); s = s.replace("1","₁"); s = s.replace("2","₂");
        s = s.replace("3","₃"); s = s.replace("4","₄"); s = s.replace("5","₅");
        s = s.replace("6","₆"); s = s.replace("7","₇"); s = s.replace("8","₈");
        boolean result = x > 130 && y < 59;
        int txtcolor = result?-1:-9735563;
        guiGraphics.pose().pushPose(); int xf =x+(nm>9?6:10); int yf =y+10;
        guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
        if (!result) {
            if (nm < 10)
                {guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:CRAFTING_TABLE_LOCATION,x+10,yf,245.0F,4.0F,8,8,256,256);
        } else {guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:CRAFTING_TABLE_LOCATION,x+6,yf,240.0F,16.0F,13,8,256,256);
        }}
        guiGraphics.drawString(font,s, x + (nm>49&&nm<60?20:20) - 2 - font.width(s), y + 8, txtcolor, false);
        guiGraphics.pose().popPose();
    }
}
