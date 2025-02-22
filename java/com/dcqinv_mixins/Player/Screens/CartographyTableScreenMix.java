package com.dcqinv_mixins.Player.Screens;

import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CartographyTableScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CartographyTableScreen.class)
public abstract class CartographyTableScreenMix extends AbstractContainerScreen<CartographyTableMenu> implements IGuiGraphics {
    public CartographyTableScreenMix(CartographyTableMenu menu, Inventory playerInventory, Component title) {super(menu, playerInventory, title);}
    private static final ResourceLocation BG_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/cartography_table.png");
    @Shadow private void renderMap(GuiGraphics guiGraphics, @Nullable MapId mapId, @Nullable MapItemSavedData mapData, int x, int y, float scale) {}
    private static final ResourceLocation ERROR_SPRITE = ResourceLocation.withDefaultNamespace("container/cartography_table/error");
    private static final ResourceLocation SCALED_MAP_SPRITE = ResourceLocation.withDefaultNamespace("container/cartography_table/scaled_map");
    private static final ResourceLocation DUPLICATED_MAP_SPRITE = ResourceLocation.withDefaultNamespace("container/cartography_table/duplicated_map");
    private static final ResourceLocation MAP_SPRITE = ResourceLocation.withDefaultNamespace("container/cartography_table/map");
    private static final ResourceLocation LOCKED_SPRITE = ResourceLocation.withDefaultNamespace("container/cartography_table/locked");
    @Override public void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

    @Overwrite
    private void renderResultingMap(GuiGraphics guiGraphics, @Nullable MapId mapId, @Nullable MapItemSavedData mapData, boolean hasMap, boolean hasPaper, boolean hasGlassPane, boolean isMaxSize) {
        int i = this.leftPos + 18;
        int j = this.topPos - 3;
        if (hasPaper && !isMaxSize) {
            guiGraphics.blitSprite(RenderType::guiTextured, SCALED_MAP_SPRITE, i + 67, j + 13, 66, 66);
            this.renderMap(guiGraphics, mapId, mapData, i + 85, j + 31, 0.226F);
        } else if (hasMap) {
            guiGraphics.blitSprite(RenderType::guiTextured, DUPLICATED_MAP_SPRITE, i + 67 + 16, j + 13, 50, 66);
            this.renderMap(guiGraphics, mapId, mapData, i + 86, j + 16, 0.34F);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 1.0F);
            guiGraphics.blitSprite(RenderType::guiTextured, DUPLICATED_MAP_SPRITE, i + 67, j + 13 + 16, 50, 66);
            this.renderMap(guiGraphics, mapId, mapData, i + 70, j + 32, 0.34F);
            guiGraphics.pose().popPose();
        } else if (hasGlassPane) {
            guiGraphics.blitSprite(RenderType::guiTextured, MAP_SPRITE, i + 67, j + 13, 66, 66);
            this.renderMap(guiGraphics, mapId, mapData, i + 71, j + 17, 0.45F);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 1.0F);
            guiGraphics.blitSprite(RenderType::guiTextured, LOCKED_SPRITE, i + 118, j + 60, 10, 14);
            guiGraphics.pose().popPose();
        } else {
            guiGraphics.blitSprite(RenderType::guiTextured, MAP_SPRITE, i + 67, j + 13, 66, 66);
            this.renderMap(guiGraphics, mapId, mapData, i + 71, j + 17, 0.45F);
        }
    }

    @Overwrite
    public void renderBg(GuiGraphics p_282101_, float p_282697_, int p_282380_, int p_282327_) {
        int i = this.leftPos;
        int j = this.topPos;
        p_282101_.blit(RenderType::guiTextured, BG_LOCATION, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        ItemStack itemstack = ((CartographyTableMenu)this.menu).getSlot(1).getItem();
        boolean flag = itemstack.is(Items.MAP);
        boolean flag1 = itemstack.is(Items.PAPER);
        boolean flag2 = itemstack.is(Items.GLASS_PANE);
        ItemStack itemstack1 = ((CartographyTableMenu)this.menu).getSlot(0).getItem();
        MapId mapid = (MapId)itemstack1.get(DataComponents.MAP_ID);
        boolean flag3 = false;
        MapItemSavedData mapitemsaveddata;
        if (mapid != null) {
            mapitemsaveddata = MapItem.getSavedData(mapid, this.minecraft.level);
            if (mapitemsaveddata != null) {
                if (mapitemsaveddata.locked) {
                    flag3 = true;
                    if (flag1 || flag2) {
                        p_282101_.blitSprite(RenderType::guiTextured, ERROR_SPRITE, i + 53, j + 30, 28, 21);
                    }
                }
                if (flag1 && mapitemsaveddata.scale >= 4) {
                    flag3 = true;
                    p_282101_.blitSprite(RenderType::guiTextured, ERROR_SPRITE, i + 53, j + 30, 28, 21);
                }
            }
        } else {
            mapitemsaveddata = null;
        }

        this.renderResultingMap(p_282101_, mapid, mapitemsaveddata, flag, flag1, flag2, flag3);
    }

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
        if (!(x > 150 && y < 65)) {
        if (nm < 10) {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:BG_LOCATION,x+10,yf,245.0F,4.0F,8,8,256,256);
        } else {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:BG_LOCATION,x+6,yf,240.0F,16.0F,13,8,256,256);
        }}
        guiGraphics.drawString(font,s, x + (nm>49&&nm<60?20:20) - 2 - font.width(s), y + 8, txtcolor, !plrInv);
        guiGraphics.pose().popPose();
    }

}
