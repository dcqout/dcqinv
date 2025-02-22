package com.dcqinv_mixins.Player.Screens;

import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import com.dcqinv.Content.PlayerGui.TradeOfferButtonClass;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMix extends AbstractContainerScreen<MerchantMenu> implements IGuiGraphics {
    public MerchantScreenMix(MerchantMenu menu, Inventory playerInventory, Component title) {super(menu, playerInventory, title);}
    @Shadow private int shopItem;@Shadow int scrollOff;@Shadow private boolean isDragging;
    @Shadow private void renderProgressBar(GuiGraphics guiGraphics, int posX, int posY, MerchantOffer merchantOffer) {}
    @Shadow private void renderButtonArrows(GuiGraphics guiGraphics, MerchantOffer merchantOffers, int posX, int posY) {}
    @Shadow private boolean canScroll(int numOffers) {return false;}@Shadow private void postButtonClick() {}
    @Shadow private void renderScroller(GuiGraphics guiGraphics, int posX, int posY, MerchantOffers merchantOffers) {}
    private static final ResourceLocation OUT_OF_STOCK_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/out_of_stock");
    private static final ResourceLocation DISCOUNT_STRIKETHRUOGH_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/discount_strikethrough");
    private static final ResourceLocation VILLAGER_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/villager.png");
    private static final Component TRADES_LABEL = Component.translatable("merchant.trades");
    private static final Component DEPRECATED_TOOLTIP = Component.translatable("merchant.deprecated");
    private final TradeOfferButtonClass[] DtradeOfferButtons = new TradeOfferButtonClass[6];
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int i = ((MerchantMenu) this.menu).getTraderLevel();
        if (i > 0 && i <= 5 && ((MerchantMenu) this.menu).showProgressBar()) {
            Component component = Component.translatable("merchant.title", new Object[]{this.title, Component.translatable("merchant.level." + i)});
            int j = this.font.width(component);
            int k = 55 + this.imageWidth / 2 - j / 2;
            guiGraphics.drawString(this.font, component, k, 6, 4210752, false);
        } else {
            guiGraphics.drawString(this.font, this.title, 55 + this.imageWidth / 2 - this.font.width(this.title) / 2, 6, 7629682, false);
        }
        int l = this.font.width(TRADES_LABEL);
        guiGraphics.drawString(this.font, TRADES_LABEL, 5 - l / 2 + 25, 6, 4210752, false);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/inventory/MerchantMenu;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/network/chat/Component;)V",
            at = @At("TAIL"))
    public void sup(MerchantMenu menu, Inventory playerInventory, Component title, CallbackInfo info) {
        this.imageWidth = 342;
        this.imageHeight = 201;
    }

    @Overwrite
    protected void init() {
        super.init();
        int i = ((this.width - this.imageWidth) / 2) + 14;
        int j = (this.height - this.imageHeight) / 2;
        int k = j + 49 + 2;
        for (int l = 0; l < 6; ++l) {
            this.DtradeOfferButtons[l] = (TradeOfferButtonClass) this.addRenderableWidget(new TradeOfferButtonClass(i,k,l, (btn) -> {
                if (btn instanceof TradeOfferButtonClass) {
                    this.shopItem = ((TradeOfferButtonClass) btn).getIndex() + this.scrollOff;
                    this.postButtonClick();
                }
            }) {@Override
            public void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
                if (this.isHovered && ((MerchantMenu) MerchantScreenMix.this.menu).getOffers().size() > this.getIndex() + MerchantScreenMix.this.scrollOff) {
                    ItemStack itemstack1;
                    if (mouseX < this.getX() + 20) {
                        itemstack1 = ((MerchantOffer) ((MerchantMenu) MerchantScreenMix.this.menu).getOffers().get(this.getIndex() + MerchantScreenMix.this.scrollOff)).getCostA();
                        guiGraphics.renderTooltip(MerchantScreenMix.this.font, itemstack1, mouseX, mouseY);
                    } else if (mouseX < this.getX() + 50 && mouseX > this.getX() + 30) {
                        itemstack1 = ((MerchantOffer) ((MerchantMenu) MerchantScreenMix.this.menu).getOffers().get(this.getIndex() + MerchantScreenMix.this.scrollOff)).getCostB();
                        if (!itemstack1.isEmpty()) {
                            guiGraphics.renderTooltip(MerchantScreenMix.this.font, itemstack1, mouseX, mouseY);
                        }
                    } else if (mouseX > this.getX() + 65) {
                        itemstack1 = ((MerchantOffer) ((MerchantMenu) MerchantScreenMix.this.menu).getOffers().get(this.getIndex() + MerchantScreenMix.this.scrollOff)).getResult();
                        guiGraphics.renderTooltip(MerchantScreenMix.this.font, itemstack1, mouseX, mouseY);
                    }
                }
            }});
            k += 24;
        }
    }

    @Overwrite
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float p_281815_) {
        super.render(guiGraphics, mouseX, mouseY, p_281815_);
        MerchantOffers merchantoffers = ((MerchantMenu) this.menu).getOffers();
        if (!merchantoffers.isEmpty()) {
            int i = (this.width - this.imageWidth) / 2;
            int j = (this.height - this.imageHeight) / 2;
            int k = j + 49 + 1;
            int l = i + 14 + 5;
            this.renderScroller(guiGraphics, i+20, j+32, merchantoffers);
            int i1 = 0;
            Iterator var11 = merchantoffers.iterator();
            while (true) {
                MerchantOffer merchantoffer;
                while (var11.hasNext()) {
                    merchantoffer = (MerchantOffer) var11.next();
                    if (this.canScroll(merchantoffers.size()) && (i1 < this.scrollOff || i1 >= 6 + this.scrollOff)) {
                        ++i1;
                    } else {
                        ItemStack itemstack = merchantoffer.getBaseCostA();
                        ItemStack itemstack1 = merchantoffer.getCostA();
                        ItemStack itemstack2 = merchantoffer.getCostB();
                        ItemStack itemstack3 = merchantoffer.getResult();
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
                        int j1 = k + 2;
                        this.renderAndDecorateCostA(guiGraphics, itemstack1, itemstack, l, j1);
                        if (!itemstack2.isEmpty()) {
                            guiGraphics.renderFakeItem(itemstack2, i + 14 + 35, j1);
                            this.renderItemCountBg(guiGraphics,i+14+35,j1,itemstack2,null,false,"");
                            guiGraphics.renderItemDecorations(this.font, itemstack2, i + 14 + 35, j1);
                        }
                        this.renderButtonArrows(guiGraphics, merchantoffer, i+9, j1);
                        guiGraphics.renderFakeItem(itemstack3, i + 14 + 68, j1);
                        this.renderItemCountBg(guiGraphics,i+14+68,j1,itemstack3,null,false,"");
                        guiGraphics.renderItemDecorations(this.font, itemstack3, i + 14 + 68, j1);
                        guiGraphics.pose().popPose();
                        k += 24;
                        ++i1;
                    }
                }
                int k1 = this.shopItem;
                merchantoffer = (MerchantOffer) merchantoffers.get(k1);
                if (((MerchantMenu) this.menu).showProgressBar()) {
                    this.renderProgressBar(guiGraphics, i+38, j, merchantoffer);
                }
                if (merchantoffer.isOutOfStock() && this.isHovering(59, 15, 22, 21, (double) mouseX, (double) mouseY) && ((MerchantMenu) this.menu).canRestock()) {
                    guiGraphics.renderTooltip(this.font, DEPRECATED_TOOLTIP, mouseX, mouseY);
                }
                TradeOfferButtonClass[] var19 = this.DtradeOfferButtons;
                int var20 = var19.length;
                for (int var21 = 0; var21 < var20; ++var21) {
                    TradeOfferButtonClass tradeofferbutton = var19[var21];
                    if (tradeofferbutton.isHoveredOrFocused()) {
                        tradeofferbutton.renderToolTip(guiGraphics, mouseX, mouseY);
                    }
                    tradeofferbutton.visible = tradeofferbutton.getIndex() < ((MerchantMenu) this.menu).getOffers().size();
                }
                break;
            }
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics p_283072_, float p_281275_, int p_282312_, int p_282984_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        p_283072_.blit(RenderType::guiTextured, VILLAGER_LOCATION, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);
        MerchantOffers merchantoffers = ((MerchantMenu)this.menu).getOffers();
        if (!merchantoffers.isEmpty()) {
            int k = this.shopItem;
            if (k < 0 || k >= merchantoffers.size()) {
                return;
            }
            MerchantOffer merchantoffer = (MerchantOffer)merchantoffers.get(k);
            if (merchantoffer.isOutOfStock()) {
                p_283072_.blitSprite(RenderType::guiTextured,OUT_OF_STOCK_SPRITE, this.leftPos + 55, this.topPos + 15, 28, 21);
            }
        }

    }

    @Overwrite
    private void renderAndDecorateCostA(GuiGraphics guiGraphics, ItemStack realCost, ItemStack baseCost, int x, int y) {
        guiGraphics.renderFakeItem(realCost, x, y);
        if (baseCost.getCount() == realCost.getCount()) {
            this.renderItemCountBg(guiGraphics,x,y,realCost,null,false,"");
            guiGraphics.renderItemDecorations(this.font, realCost, x, y);
        } else {
            this.renderItemCountBg(guiGraphics,x,y, baseCost,null,false,null);
            this.renderItemCountBg(guiGraphics,x+14,y,realCost,null,false,"");
            guiGraphics.renderItemDecorations(this.font, baseCost, x, y, baseCost.getCount() == 1 ? "1" : null);
            //String count = realCost.getCount() == 1 ? "1" : String.valueOf(realCost.getCount());
            //guiGraphics.drawString(this.font, count, (float)(x + 14) + 19.0F - 2.0F - (float)this.font.width(count), (float)y + 6.0F + 3.0F, 16777215, true);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 300.0F);
            guiGraphics.blitSprite(RenderType::guiTextured, DISCOUNT_STRIKETHRUOGH_SPRITE, x + 7, y + 12, 9, 2);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public boolean mouseScrolled(double p_99127_, double p_99128_, double p_99129_, double p_295610_) {
        if (super.mouseScrolled(p_99127_, p_99128_, p_99129_, p_295610_)) {
            return true;
        } else {
            int i = ((MerchantMenu)this.menu).getOffers().size();
            if (this.canScroll(i)) {
                int j = i - 6;
                this.scrollOff = Mth.clamp((int)((double)this.scrollOff - p_295610_), 0, j);
            }
            return true;
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        int i = ((MerchantMenu)this.menu).getOffers().size();
        if (this.isDragging) {
            int j = this.topPos + 50;
            int k = j + 139;
            int l = i - 6;
            float f = ((float)mouseY - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
            f = f * (float)l + 0.5F;
            this.scrollOff = Mth.clamp((int)f, 0, l);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.isDragging = false;
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (this.canScroll(((MerchantMenu)this.menu).getOffers().size()) && mouseX > (double)(i + 114) && mouseX < (double)(i + 114 + 6) && mouseY > (double)(j + 50) && mouseY <= (double)(j + 50 + 139 + 1)) {
            this.isDragging = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderItemCountBg(GuiGraphics guiGraphics, int x, int y, ItemStack stack, String countString, boolean plrInv, String title) { int nm = stack.getCount();
        if (!(nm > 1)) {if (countString != null) {nm = Integer.parseInt(countString);} else {return;}}
        String s = countString == null ? String.valueOf(nm) : countString; if (title != null) {
        s = s.replace("9", "₉");s = s.replace("0", "₀");s = s.replace("1", "₁");
        s = s.replace("2", "₂");s = s.replace("3", "₃");s = s.replace("4", "₄");s = s.replace("5", "₅");
        s = s.replace("6", "₆");s = s.replace("7", "₇");s = s.replace("8", "₈");
        }
        boolean result = x > 80 && y < 48; boolean andTrade = !plrInv && y > 48;
        int txtcolor = result || andTrade ? -1 : -9735563;
        guiGraphics.pose().pushPose();int xf = x + (nm > 9 ? 6 : 10);int yf = y + 10;
        guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
        if (!result && !andTrade) {
            if (nm < 10) {
                guiGraphics.blit(RenderType::guiTextured,INVENTORY_LOCATION, x + 10, yf, 245.0F, 4.0F, 8, 8, 256, 256);
            } else {
                guiGraphics.blit(RenderType::guiTextured,INVENTORY_LOCATION, x + 6, yf, 240.0F, 16.0F, 13, 8, 256, 256);
            }
        }
        guiGraphics.drawString(font, s, x + (nm > 49 && nm < 60 ? 20 : 20) - 2 - font.width(s), y + 8, txtcolor, andTrade);
        guiGraphics.pose().popPose();
    }
}
