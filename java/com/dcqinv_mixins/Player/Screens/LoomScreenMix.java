package com.dcqinv_mixins.Player.Screens;

import com.mojang.blaze3d.platform.Lighting;
import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LoomScreen.class)
public abstract class LoomScreenMix extends AbstractContainerScreen<LoomMenu> implements IGuiGraphics {
    public LoomScreenMix(LoomMenu menu, Inventory playerInventory, Component title) {super(menu, playerInventory, title);}
    private static final ResourceLocation BG_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/loom.png");
    @Override public void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}
    private static final ResourceLocation BANNER_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot/banner");
    private static final ResourceLocation DYE_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot/dye");
    private static final ResourceLocation PATTERN_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot/banner_pattern");
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/loom/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/loom/scroller_disabled");
    private static final ResourceLocation PATTERN_SELECTED_SPRITE = ResourceLocation.withDefaultNamespace("container/loom/pattern_selected");
    private static final ResourceLocation PATTERN_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("container/loom/pattern_highlighted");
    private static final ResourceLocation PATTERN_SPRITE = ResourceLocation.withDefaultNamespace("container/loom/pattern");
    private static final ResourceLocation ERROR_SPRITE = ResourceLocation.withDefaultNamespace("container/loom/error");
    @Shadow private ModelPart flag; @Shadow private BannerPatternLayers resultBannerPatterns; @Shadow private boolean displayPatterns;
    @Shadow private boolean hasMaxPatterns; @Shadow private float scrollOffs; @Shadow private int startRow; @Shadow private boolean scrolling;
    @Shadow private void renderPattern(GuiGraphics guiGraphics, Holder<BannerPattern> patern, int x, int y) {}
    @Shadow private int totalRowCount() {return 0;}

    @Overwrite
    public void renderBg(GuiGraphics guiGraphics, float p_281777_, int p_283331_, int p_283087_) {
        int i = this.leftPos;
        int j = this.topPos;
        guiGraphics.blit(RenderType::guiTextured, BG_LOCATION, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        Slot slot = ((LoomMenu)this.menu).getBannerSlot(); Slot slot1 = ((LoomMenu)this.menu).getDyeSlot();
        Slot slot2 = ((LoomMenu)this.menu).getPatternSlot(); Slot slot3 = ((LoomMenu)this.menu).getResultSlot();
        if (!slot.hasItem()) { guiGraphics.blitSprite(RenderType::guiTextured, BANNER_SLOT_SPRITE, i + slot.x, j + slot.y, 16, 16); }
        if (!slot1.hasItem()) { guiGraphics.blitSprite(RenderType::guiTextured, DYE_SLOT_SPRITE, i + slot1.x, j + slot1.y, 16, 16); }
        if (!slot2.hasItem()) { guiGraphics.blitSprite(RenderType::guiTextured, PATTERN_SLOT_SPRITE, i + slot2.x, j + slot2.y, 16, 16);}
        int k = (int)(41.0F * this.scrollOffs);
        ResourceLocation resourcelocation = this.displayPatterns ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
        guiGraphics.blitSprite(RenderType::guiTextured, resourcelocation, i + 141, j + 7 + k, 12, 15);
        guiGraphics.flush();
        Lighting.setupForFlatItems();
        if (this.resultBannerPatterns != null && !this.hasMaxPatterns) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate((float)(i + 161), (float)(j + 50), 0.0F);
            guiGraphics.pose().scale(24.0F, 24.0F, 1.0F);
            guiGraphics.pose().translate(0.5F, 0.0F, 0.5F);
            float f = 0.6666667F;
            guiGraphics.pose().scale(0.6666667F, 0.6666667F, -0.6666667F);
            DyeColor dyecolor = ((BannerItem)slot3.getItem().getItem()).getColor();
            guiGraphics.drawSpecial((p_371285_) -> {
                BannerRenderer.renderPatterns(guiGraphics.pose(), p_371285_, 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, dyecolor, this.resultBannerPatterns);
            });
            guiGraphics.pose().popPose();
        } else if (this.hasMaxPatterns) {
            guiGraphics.blitSprite(RenderType::guiTextured, ERROR_SPRITE, i + slot3.x - 5, j + slot3.y - 5, 26, 26);
        }
        if (this.displayPatterns) {
            int j2 = i + 82;
            int k2 = j + 7;
            List<Holder<BannerPattern>> list = ((LoomMenu)this.menu).getSelectablePatterns();
            label63:
            for(int l = 0; l < 4; ++l) {
                for(int i1 = 0; i1 < 4; ++i1) {
                    int j1 = l + this.startRow;
                    int k1 = j1 * 4 + i1;
                    if (k1 >= list.size()) {
                        break label63;
                    }
                    int l1 = j2 + i1 * 14;
                    int i2 = k2 + l * 14;
                    boolean flag = p_283331_ >= l1 && p_283087_ >= i2 && p_283331_ < l1 + 14 && p_283087_ < i2 + 14;
                    ResourceLocation resourcelocation1; boolean selec = k1 == ((LoomMenu)this.menu).getSelectedBannerPatternIndex();
                    if (selec) {
                        resourcelocation1 = PATTERN_SELECTED_SPRITE;
                    } else if (flag) {
                        resourcelocation1 = PATTERN_HIGHLIGHTED_SPRITE;
                    } else {
                        resourcelocation1 = PATTERN_SPRITE;
                    }
                    guiGraphics.blitSprite(RenderType::guiTextured, resourcelocation1, l1, i2, 14, 14);
                    this.renderPattern(guiGraphics, (Holder)list.get(k1), l1, selec || flag?i2-1:i2);
                }
            }
        }
        guiGraphics.flush();
        Lighting.setupFor3DItems();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolling = false;
        if (this.displayPatterns) {
            int i = this.leftPos + 82;
            int j = this.topPos + 7;
            for(int k = 0; k < 4; ++k) {
                for(int l = 0; l < 4; ++l) {
                    double d0 = mouseX - (double)(i + l * 14);
                    double d1 = mouseY - (double)(j + k * 14);
                    int i1 = k + this.startRow;
                    int j1 = i1 * 4 + l;
                    if (d0 >= 0.0 && d1 >= 0.0 && d0 < 14.0 && d1 < 14.0 && ((LoomMenu)this.menu).clickMenuButton(this.minecraft.player, j1)) {
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
                        this.minecraft.gameMode.handleInventoryButtonClick(((LoomMenu)this.menu).containerId, j1);
                        return true;
                    }
                }
            }
            i = this.leftPos + 141;
            j = this.topPos + 9;
            if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + 56)) {
                this.scrolling = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        int i = this.totalRowCount() - 4;
        if (this.scrolling && this.displayPatterns && i > 0) {
            int j = this.topPos + 7;
            int k = j + 56;
            this.scrollOffs = ((float)mouseY - (float)j - 7.5F) / ((float)(k - j) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.startRow = Math.max((int)((double)(this.scrollOffs * (float)i) + 0.5), 0);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
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
            if (nm < 10)
            {
                guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:BG_LOCATION,x+10,yf,245.0F,4.0F,8,8,256,256);
            } else {
                guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:BG_LOCATION,x+6,yf,240.0F,16.0F,13,8,256,256);
            }
        guiGraphics.drawString(font,s, x + (nm>49&&nm<60?20:20) - 2 - font.width(s), y + 8, txtcolor, !plrInv);
        guiGraphics.pose().popPose();
    }
}
