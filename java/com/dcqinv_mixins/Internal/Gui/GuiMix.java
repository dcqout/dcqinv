package com.dcqinv_mixins.Internal.Gui;

import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Gui.class)
public class GuiMix {

    public GuiMix(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Nullable
    @Shadow private Player getCameraPlayer() {return null;} @Shadow private final Minecraft minecraft;
    @Shadow private static final ResourceLocation HOTBAR_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar");
    @Shadow private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_selection");
    @Shadow private static final ResourceLocation HOTBAR_OFFHAND_LEFT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_left");
    @Shadow private static final ResourceLocation HOTBAR_OFFHAND_RIGHT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_right");
    @Shadow    private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace(
            "hud/hotbar_attack_indicator_background"
    );
    @Shadow private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace(
            "hud/hotbar_attack_indicator_progress"
    );

    @Overwrite
    private void renderSlot(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, int seed) {
        if (!stack.isEmpty()) {
            float f = (float)stack.getPopTime() - deltaTracker.getGameTimeDeltaPartialTick(false);
            if (f > 0.0F) {
                float f1 = 1.0F + f / 5.0F;
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate((float)(x + 8), (float)(y + 12), 0.0F);
                guiGraphics.pose().scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                guiGraphics.pose().translate((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
            }
            guiGraphics.renderItem(player, stack, x, y, seed);
            if (f > 0.0F) {
                guiGraphics.pose().popPose();
            }
            guiGraphics.renderItemDecorations(this.minecraft.font, stack, x, y); int nm = stack.getCount(); if (!(nm > 1)) {return;}
            String s = String.valueOf(nm); s = s.replace("9","₉");
            s = s.replace("0","₀"); s = s.replace("1","₁"); s = s.replace("2","₂");
            s = s.replace("3","₃"); s = s.replace("4","₄"); s = s.replace("5","₅");
            s = s.replace("6","₆"); s = s.replace("7","₇"); s = s.replace("8","₈");
            guiGraphics.pose().pushPose(); int xf =x+(nm>9?6:10); int yf =y+10;
            guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
            if (nm < 10) {
                guiGraphics.blitSprite(RenderType::guiTextured, HOTBAR_SPRITE,205,22,194,2, x+10,yf,8,8);
            } else {
                guiGraphics.blitSprite(RenderType::guiTextured, HOTBAR_SPRITE,205,22,189,12, x+6,yf,12,8);
            }
            guiGraphics.drawString(this.minecraft.font,s, x + 19 - 2 - this.minecraft.font.width(s), y + 8,-14210515, false);
            guiGraphics.pose().popPose();
        }
    }


    @Overwrite
    private void renderItemHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Player player = this.getCameraPlayer();
        if (player != null) {
            ItemStack itemstack = player.getOffhandItem();
            HumanoidArm humanoidarm = player.getMainArm().getOpposite();
            int i = guiGraphics.guiWidth() / 2;
            int j = 182;
            int k = 91;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, -90.0F);
            guiGraphics.blitSprite(RenderType::guiTextured, HOTBAR_SPRITE,205,22,-23,0, i - 91 - 23, guiGraphics.guiHeight() - 22,205,22);
            guiGraphics.pose().translate(0.0F, 0.0F, 300.0F);
            guiGraphics.blitSprite(
                    RenderType::guiTextured, HOTBAR_SELECTION_SPRITE, i - 91 - 1 + player.getInventory().selected * 20, guiGraphics.guiHeight() - 22 - 1, 24, 23
            );
            guiGraphics.pose().translate(0.0F, 0.0F, -90.0F);
            if (!itemstack.isEmpty()) {
                if (humanoidarm == HumanoidArm.LEFT) {
                    guiGraphics.blitSprite(RenderType::guiTextured, HOTBAR_OFFHAND_LEFT_SPRITE, i - 91 - 29, guiGraphics.guiHeight() - 23, 29, 24);
                } else {
                    guiGraphics.blitSprite(RenderType::guiTextured, HOTBAR_OFFHAND_RIGHT_SPRITE, i + 91, guiGraphics.guiHeight() - 23, 29, 24);
                }
            }

            guiGraphics.pose().popPose();
            int l = 1;

            for (int i1 = 0; i1 < 9; i1++) {
                int j1 = i - 90 + i1 * 20 + 2;
                int k1 = guiGraphics.guiHeight() - 16 - 3;
                this.renderSlot(guiGraphics, j1, k1, deltaTracker, player, player.getInventory().items.get(i1), l++);
            }

            if (!itemstack.isEmpty()) {
                int i2 = guiGraphics.guiHeight() - 16 - 3;
                if (humanoidarm == HumanoidArm.LEFT) {
                    this.renderSlot(guiGraphics, i - 91 - 26, i2, deltaTracker, player, itemstack, l++);
                } else {
                    this.renderSlot(guiGraphics, i + 91 + 10, i2, deltaTracker, player, itemstack, l++);
                }
            }

            if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
                float f = this.minecraft.player.getAttackStrengthScale(0.0F);
                if (f < 1.0F) {
                    int j2 = guiGraphics.guiHeight() - 20;
                    int k2 = i + 91 + 6;
                    if (humanoidarm == HumanoidArm.RIGHT) {
                        k2 = i - 91 - 22;
                    }

                    int l1 = (int)(f * 19.0F);
                    guiGraphics.blitSprite(RenderType::guiTextured, HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE, k2, j2, 18, 18);
                    guiGraphics.blitSprite(RenderType::guiTextured, HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE, 18, 18, 0, 18 - l1, k2, j2 + 18 - l1, 18, l1);
                }
            }
        }
    }
}
