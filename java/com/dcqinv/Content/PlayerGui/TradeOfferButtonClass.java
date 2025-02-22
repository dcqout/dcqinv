package com.dcqinv.Content.PlayerGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class TradeOfferButtonClass extends Button {
    final int index;
    protected static final WidgetSprites BUTTONS = new WidgetSprites(ResourceLocation.withDefaultNamespace("widget/button"),
            ResourceLocation.withDefaultNamespace("widget/button_disabled"),
            ResourceLocation.withDefaultNamespace("widget/button_highlighted"));
    public TradeOfferButtonClass(int x, int y, int index, OnPress onPress) {
        super(x, y, 88, 20, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        this.index = index;
        this.visible = false;
    }
    public TradeOfferButtonClass newInstance(int x,int y,int ix, OnPress onPress) {
        return new TradeOfferButtonClass(x,y,ix,onPress);
    }
    @Override
    public void renderWidget(GuiGraphics p_281670_, int p_282682_, int p_281714_, float p_282542_) {
        Minecraft minecraft = Minecraft.getInstance();
        p_281670_.blitSprite(RenderType::guiTextured, BUTTONS.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
        int i = this.active ? 16777215 : 10526880;
        this.renderString(p_281670_, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
    }
    public int getIndex() { return this.index; }
    public void renderToolTip(GuiGraphics guiGraphics, int mouseX, int mouseY) {}
}
