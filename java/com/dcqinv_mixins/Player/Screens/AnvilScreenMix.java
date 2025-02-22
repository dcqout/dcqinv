package com.dcqinv_mixins.Player.Screens;

import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AnvilScreen.class) @SuppressWarnings("UnreachableCode")
public abstract class AnvilScreenMix extends ItemCombinerScreen<AnvilMenu> implements IGuiGraphics {
    private static final ResourceLocation ANVIL_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/anvil.png");
    private static final ResourceLocation TEXT_FIELD_SPRITE = ResourceLocation.withDefaultNamespace("container/anvil/text_field");
    private static final ResourceLocation ERROR_SPRITE = ResourceLocation.withDefaultNamespace("container/anvil/error");
    private static final ResourceLocation TEXT_FIELD_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/anvil/text_field_disabled");
    @Shadow private static final Component TOO_EXPENSIVE_TEXT = Component.translatable("container.repair.expensive");
    @Shadow private final Player player; @Shadow private EditBox name; @Shadow private void onNameChanged(String name) {};

    public AnvilScreenMix(AnvilMenu menu, Inventory playerInventory, Component title, ResourceLocation menuResource) {
        super(menu, playerInventory, title, menuResource);
        player = this.minecraft.player;
    }

    @Override
    protected void renderBg(GuiGraphics p_283345_, float p_283412_, int p_282871_, int p_281306_) {
        super.renderBg(p_283345_, p_283412_, p_282871_, p_281306_);
        p_283345_.blitSprite(RenderType::guiTextured, ((AnvilMenu)this.menu).getSlot(0).hasItem() ? TEXT_FIELD_SPRITE : TEXT_FIELD_DISABLED_SPRITE, this.leftPos + 60, this.topPos + 19, 110, 16);
    }

    @Override
    protected void subInit() {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.name = new EditBox(this.font, i + 63, j + 23, 103, 12, Component.translatable("container.repair"));
        this.name.setCanLoseFocus(false);
        this.name.setTextColor(-1);
        this.name.setTextColorUneditable(-1);
        this.name.setBordered(false);
        this.name.setMaxLength(50);
        this.name.setResponder(this::onNameChanged);
        this.name.setValue("");
        this.addWidget(this.name);
        this.name.setEditable(((AnvilMenu)this.menu).getSlot(0).hasItem());
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int p_282417_, int p_283022_) {
        AnvilScreen caster = (AnvilScreen)(Object)this;
        guiGraphics.drawString(this.font, this.title, this.titleLabelX+20, this.titleLabelY+3, 4210752, false);
        int i = this.menu.getCost();
        if (i > 0) {
            int j = 8453920;
            Component component;
            if (i >= 40 && !this.minecraft.player.getAbilities().instabuild) {
                component = TOO_EXPENSIVE_TEXT;
                j = 16736352;
            } else if (!this.menu.getSlot(2).hasItem()) {
                component = null;
            } else {
                component = Component.translatable("container.repair.cost", i);
                if (!this.menu.getSlot(2).mayPickup(player)) {
                    j = 16736352;
                }
            }
            if (component != null) {
                int k = (this.imageWidth/2) + (this.font.width(component)/2) - this.font.width(component);
                int l = 69;
                guiGraphics.fill(k - 2, 67, k + this.font.width(component) + 2, 79, 1325400064);
                guiGraphics.drawString(this.font, component, k, 69, j);
            }
        }
    }

    @Override
    protected void renderErrorIcon(GuiGraphics guiGraphics, int i, int i1) {
        if ((((AnvilMenu)this.menu).getSlot(0).hasItem() || ((AnvilMenu)this.menu).getSlot(1).hasItem()) && !((AnvilMenu)this.menu).getSlot(((AnvilMenu)this.menu).getResultSlot()).hasItem()) {
            guiGraphics.blitSprite(RenderType::guiTextured, ERROR_SPRITE, i + 118, i1 + 44, 28, 21);
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
        if (nm < 10) {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:ANVIL_LOCATION,x+10,yf,245.0F,4.0F,8,8,256,256);
        } else {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:ANVIL_LOCATION,x+6,yf,240.0F,16.0F,13,8,256,256);
        }
        guiGraphics.drawString(font,s, x + (nm>49&&nm<60?20:20) - 2 - font.width(s), y + 8, txtcolor, !plrInv);
        guiGraphics.pose().popPose();
    }
}
