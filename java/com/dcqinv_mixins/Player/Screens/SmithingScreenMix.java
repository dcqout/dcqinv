package com.dcqinv_mixins.Player.Screens;

import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(SmithingScreen.class)
public abstract class SmithingScreenMix extends ItemCombinerScreen<SmithingMenu> implements IGuiGraphics {
    public SmithingScreenMix(SmithingMenu menu, Inventory playerInventory, Component title, ResourceLocation menuResource) {super(menu, playerInventory, title, menuResource);}
    @Shadow private final CyclingSlotBackground templateIcon = new CyclingSlotBackground(0);
    @Shadow private final CyclingSlotBackground baseIcon = new CyclingSlotBackground(1);
    @Shadow private final CyclingSlotBackground additionalIcon = new CyclingSlotBackground(2);
    @Shadow @Nullable private ArmorStand armorStandPreview; @Shadow private boolean hasRecipeError() {return false;}
    @Shadow private static final Vector3f ARMOR_STAND_TRANSLATION; @Shadow private static final Quaternionf ARMOR_STAND_ANGLE;
    private static final ResourceLocation ERROR_SPRITE = ResourceLocation.withDefaultNamespace("container/smithing/error");
    private static final ResourceLocation CONTAINER_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/smithing.png");
    private static final Component MISSING_TEMPLATE_TOOLTIP = Component.translatable("container.upgrade.missing_template_tooltip");
    private static final Component ERROR_TOOLTIP = Component.translatable("container.upgrade.error_tooltip");
    static { ARMOR_STAND_TRANSLATION = new Vector3f(); ARMOR_STAND_ANGLE = (new Quaternionf()).rotationXYZ(0.43633232F, 0.0F, 3.1415927F);}

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX+24, this.titleLabelY-2, 7629682, false);
    }

    @Override
    protected void renderErrorIcon(GuiGraphics p_281835_, int p_283389_, int p_282634_) {
        if (this.hasRecipeError()) {
            p_281835_.blitSprite(RenderType::guiTextured, ERROR_SPRITE, p_283389_ + 90, p_282634_ + 46, 28, 21);
        }

    }

    @Overwrite
    private void renderOnboardingTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Optional<Component> optional = Optional.empty();
        if (this.hasRecipeError() && this.isHovering(90, 46, 28, 21, (double)mouseX, (double)mouseY)) {
            optional = Optional.of(ERROR_TOOLTIP);
        }
        if (this.hoveredSlot != null) {
            ItemStack itemstack = ((SmithingMenu)this.menu).getSlot(0).getItem();
            ItemStack itemstack1 = this.hoveredSlot.getItem();
            if (itemstack.isEmpty()) {
                if (this.hoveredSlot.index == 0) {
                    optional = Optional.of(MISSING_TEMPLATE_TOOLTIP);
                }
            } else {
                Item var8 = itemstack.getItem();
                if (var8 instanceof SmithingTemplateItem) {
                    SmithingTemplateItem smithingtemplateitem = (SmithingTemplateItem)var8;
                    if (itemstack1.isEmpty()) {
                        if (this.hoveredSlot.index == 1) {
                            optional = Optional.of(smithingtemplateitem.getBaseSlotDescription());
                        } else if (this.hoveredSlot.index == 2) {
                            optional = Optional.of(smithingtemplateitem.getAdditionSlotDescription());
                        }
                    }
                }
            }
        }

        optional.ifPresent((p_280863_) -> {
            guiGraphics.renderTooltip(this.font, this.font.split(p_280863_, 115), mouseX, mouseY);
        });
    }

    @Overwrite
    protected void renderBg(GuiGraphics p_283264_, float p_267158_, int p_267266_, int p_266722_) {
        super.renderBg(p_283264_, p_267158_, p_267266_, p_266722_);
        this.templateIcon.render(this.menu, p_283264_, p_267158_, this.leftPos, this.topPos);
        this.baseIcon.render(this.menu, p_283264_, p_267158_, this.leftPos, this.topPos);
        this.additionalIcon.render(this.menu, p_283264_, p_267158_, this.leftPos, this.topPos);
        InventoryScreen.renderEntityInInventory(p_283264_, (float)(this.leftPos + 170), (float)(this.topPos + 70), 35.0F,
                ARMOR_STAND_TRANSLATION, ARMOR_STAND_ANGLE, (Quaternionf)null, this.armorStandPreview);
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
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:CONTAINER_LOCATION,x+10,yf,245.0F,4.0F,8,8,256,256);
        } else {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:CONTAINER_LOCATION,x+6,yf,240.0F,16.0F,13,8,256,256);
        }
        guiGraphics.drawString(font,s, x + (nm>49&&nm<60?20:20) - 2 - font.width(s), y + 8, txtcolor, !plrInv);
        guiGraphics.pose().popPose();
    }

}
