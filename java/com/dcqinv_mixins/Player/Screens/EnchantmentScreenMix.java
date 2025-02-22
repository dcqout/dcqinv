package com.dcqinv_mixins.Player.Screens;

import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentScreen.class)
public abstract class EnchantmentScreenMix extends AbstractContainerScreen<EnchantmentMenu> implements IGuiGraphics {
    public EnchantmentScreenMix(EnchantmentMenu menu, Inventory playerInventory, Component title) {super(menu, playerInventory, title);}
    private static final ResourceLocation[] ENABLED_LEVEL_SPRITES = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/enchanting_table/level_1"), ResourceLocation.withDefaultNamespace("container/enchanting_table/level_2"), ResourceLocation.withDefaultNamespace("container/enchanting_table/level_3")};
    private static final ResourceLocation[] DISABLED_LEVEL_SPRITES = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/enchanting_table/level_1_disabled"), ResourceLocation.withDefaultNamespace("container/enchanting_table/level_2_disabled"), ResourceLocation.withDefaultNamespace("container/enchanting_table/level_3_disabled")};
    private static final ResourceLocation ENCHANTING_TABLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/enchanting_table.png");
    private static final ResourceLocation ENCHANTMENT_SLOT_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/enchanting_table/enchantment_slot_disabled");
    private static final ResourceLocation ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("container/enchanting_table/enchantment_slot_highlighted");
    private static final ResourceLocation ENCHANTMENT_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/enchanting_table/enchantment_slot");
    @Shadow private void renderBook(GuiGraphics guiGraphics, int x, int y, float partialTick) {}@Override public void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}
    @Inject(method = "<init>",at = @At("TAIL"))
    public void entable(EnchantmentMenu menu, Inventory playerInventory, Component title, CallbackInfo info) {
        this.imageWidth = 215; this.imageHeight = 205;
    }
    @Override
    public void renderBg(GuiGraphics p_282430_, float p_282530_, int p_281621_, int p_283333_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        p_282430_.blit(RenderType::guiTextured, ENCHANTING_TABLE_LOCATION, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        this.renderBook(p_282430_, i+17, j, p_282530_);
        EnchantmentNames.getInstance().initSeed((long)((EnchantmentMenu)this.menu).getEnchantmentSeed());
        int k = ((EnchantmentMenu)this.menu).getGoldCount();
        for(int l = 0; l < 3; ++l) {
            int i1 = i + 75;
            int j1 = i1 + 20;
            int k1 = ((EnchantmentMenu)this.menu).costs[l];
            if (k1 == 0) {
                p_282430_.blitSprite(RenderType::guiTextured, ENCHANTMENT_SLOT_DISABLED_SPRITE, i1, j + 18 + 19 * l, 108, 19);
            } else {
                String s = "" + k1;
                int l1 = 86 - this.font.width(s);
                FormattedText formattedtext = EnchantmentNames.getInstance().getRandomName(this.font, l1);
                int i2 = 6839882;
                if ((k >= l + 1 && this.minecraft.player.experienceLevel >= k1 || this.minecraft.player.getAbilities().instabuild) && ((EnchantmentMenu)this.menu).enchantClue[l] != -1) {
                    int j2 = p_281621_ - (i + 75);
                    int k2 = p_283333_ - (j + 18 + 19 * l);
                    if (j2 >= 0 && k2 >= 0 && j2 < 108 && k2 < 19) {
                        p_282430_.blitSprite(RenderType::guiTextured, ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE, i1, j + 18 + 19 * l, 108, 19);
                        i2 = 16777088;
                    } else {
                        p_282430_.blitSprite(RenderType::guiTextured, ENCHANTMENT_SLOT_SPRITE, i1, j + 18 + 19 * l, 108, 19);
                    }
                    p_282430_.blitSprite(RenderType::guiTextured, ENABLED_LEVEL_SPRITES[l], i1 + 1, j + 19 + 19 * l, 16, 16);
                    p_282430_.drawWordWrap(this.font, formattedtext, j1, j + 20 + 19 * l, l1, i2, false);
                    i2 = 8453920;
                } else {
                    p_282430_.blitSprite(RenderType::guiTextured, ENCHANTMENT_SLOT_DISABLED_SPRITE, i1, j + 18 + 19 * l, 108, 19);
                    p_282430_.blitSprite(RenderType::guiTextured, DISABLED_LEVEL_SPRITES[l], i1 + 1, j + 19 + 19 * l, 16, 16);
                    p_282430_.drawWordWrap(this.font, formattedtext, j1, j + 20 + 19 * l, l1, (i2 & 16711422) >> 1, false);
                    i2 = 4226832;
                }
                p_282430_.drawString(this.font, s, j1 + 86 - this.font.width(s), j + 20 + 19 * l + 7, i2);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        for(int k = 0; k < 3; ++k) {
            double d0 = mouseX - (double)(i + 75);
            double d1 = mouseY - (double)(j + 18 + 19 * k);
            if (d0 >= 0.0 && d1 >= 0.0 && d0 < 108.0 && d1 < 19.0 && ((EnchantmentMenu)this.menu).clickMenuButton(this.minecraft.player, k)) {
                this.minecraft.gameMode.handleInventoryButtonClick(((EnchantmentMenu)this.menu).containerId, k);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
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
        if (nm < 10) {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:ENCHANTING_TABLE_LOCATION,x+10,yf,245.0F,4.0F,8,8,256,256);
        } else {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:ENCHANTING_TABLE_LOCATION,x+6,yf,240.0F,16.0F,13,8,256,256);
        }
        guiGraphics.drawString(font,s, x + (nm>49&&nm<60?20:20) - 2 - font.width(s), y + 8, txtcolor, !plrInv);
        guiGraphics.pose().popPose();
    }
}
