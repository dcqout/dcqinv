package com.dcqinv_mixins.Player.Screens;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconScreen.class)
public abstract class BeaconScreenMix extends AbstractContainerScreen<BeaconMenu> implements IGuiGraphics {
    public BeaconScreenMix(BeaconMenu menu, Inventory playerInventory, Component title) {super(menu, playerInventory, title);}
    private static final ResourceLocation BEACON_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/beacon.png");
    private static final Component PRIMARY_EFFECT_LABEL = Component.translatable("block.minecraft.beacon.primary");
    private static final Component SECONDARY_EFFECT_LABEL = Component.translatable("block.minecraft.beacon.secondary");

    @Overwrite
    protected void renderLabels(GuiGraphics p_283369_, int p_282699_, int p_281296_) {
        p_283369_.drawCenteredString(this.font, PRIMARY_EFFECT_LABEL, 75, 12, 14737632);
        p_283369_.drawCenteredString(this.font, SECONDARY_EFFECT_LABEL, 175, 12, 14737632);
    }

    @Inject(method = "<init>",at = @At("TAIL"))
    public void setImg(BeaconMenu menu, Inventory playerInventory, Component title, CallbackInfo info) {
        this.imageHeight = 242; this.imageWidth = 243;
    }

    /*@ModifyExpressionValue(method = "init()V",at = @At(ordinal = 1,value = "FIELD"))
    public int Confirm_leftPos(int aw) { return this.leftPos - 10;}*/
    @ModifyExpressionValue(method = "init()V",at = @At(ordinal = 2,value = "FIELD"))
    public int Confirm_topPos(int aw) { return this.topPos - 4;}
    /*@ModifyExpressionValue(method = "init()V",at = @At(ordinal = 3,value = "FIELD"))
    public int Cancel_leftPos(int aw) { return this.topPos - 4;}*/
    @ModifyExpressionValue(method = "init()V",at = @At(ordinal = 4,value = "FIELD"))
    public int Cancel_topPos(int aw) { return this.topPos - 4;}

    @Overwrite
    protected void renderBg(GuiGraphics p_282454_, float p_282185_, int p_282362_, int p_282987_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        p_282454_.blit(RenderType::guiTextured, BEACON_LOCATION, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        p_282454_.pose().pushPose();
        p_282454_.pose().translate(0.0F, 0.0F, 100.0F);
        p_282454_.renderItem(new ItemStack(Items.NETHERITE_INGOT), i + 20, j + 106);
        p_282454_.renderItem(new ItemStack(Items.EMERALD), i + 41, j + 106);
        p_282454_.renderItem(new ItemStack(Items.DIAMOND), i + 41 + 22, j + 106);
        p_282454_.renderItem(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 106);
        p_282454_.renderItem(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 106);
        p_282454_.pose().popPose();
    }

    @Override
    public void renderItemCountBg(GuiGraphics guiGraphics, int x, int y, ItemStack stack, String countString, boolean plrInv, String title) { int nm = stack.getCount();
        if (!(nm > 1)) { if (countString != null) { nm = Integer.parseInt(countString); } else { return; } }
        String s = countString == null ? String.valueOf(nm) : countString; s = s.replace("9","₉");
        s = s.replace("0","₀"); s = s.replace("1","₁"); s = s.replace("2","₂");
        s = s.replace("3","₃"); s = s.replace("4","₄"); s = s.replace("5","₅");
        s = s.replace("6","₆"); s = s.replace("7","₇"); s = s.replace("8","₈");
        guiGraphics.pose().pushPose(); int xf =x+(nm>9?6:10); int yf =y+10;
        guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
        if (nm < 10) {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:BEACON_LOCATION,x+10,yf,245.0F,4.0F,8,8,256,256);
        } else {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:BEACON_LOCATION,x+6,yf,240.0F,16.0F,13,8,256,256);
        }
        guiGraphics.drawString(font,s, x + (nm>49&&nm<60?20:20) - 2 - font.width(s), y + 8, -9735563, false);
        guiGraphics.pose().popPose();
    }
}
