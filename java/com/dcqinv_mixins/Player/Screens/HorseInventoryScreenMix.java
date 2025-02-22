package com.dcqinv_mixins.Player.Screens;

import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HorseInventoryScreen.class)
public abstract class HorseInventoryScreenMix extends AbstractContainerScreen<HorseInventoryMenu> implements IGuiGraphics {
    public HorseInventoryScreenMix(HorseInventoryMenu menu, Inventory playerInventory, Component title) {super(menu, playerInventory, title);
        horse = null;inventoryColumns = 0;
    }
    private static final ResourceLocation HORSE_INVENTORY_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/horse.png");
    private static final ResourceLocation CHEST_SLOTS_SPRITE = ResourceLocation.withDefaultNamespace("container/horse/chest_slots");
    @Shadow private final AbstractHorse horse; @Shadow private final int inventoryColumns; @Shadow private void drawSlot(GuiGraphics guiGraphics, int x, int y) {}
    @Shadow private float xMouse; @Shadow private float yMouse;

    @Override
    public void renderBg(GuiGraphics p_282553_, float p_282998_, int p_282929_, int p_283133_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        p_282553_.blit(RenderType::guiTextured, HORSE_INVENTORY_LOCATION, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        if (this.inventoryColumns > 0) {
            p_282553_.blitSprite(RenderType::guiTextured, CHEST_SLOTS_SPRITE, 100, 56, 0, 0, i + 109, j + 13, (this.inventoryColumns*2) + this.inventoryColumns * 18, 56);
        }

        if (this.horse.isSaddleable()) {
            this.drawSlot(p_282553_, i + 9, j + 35 - 22);
        }

        if (this.horse.canUseSlot(EquipmentSlot.BODY)) {
            this.drawSlot(p_282553_, i + 9, j + 33);
        }

        InventoryScreen.renderEntityInInventoryFollowsMouse(p_282553_, i + 35, j + 15, i + 87, j + 67, 17, 0.25F, this.xMouse, this.yMouse, this.horse);
    }

    @Override
    public void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX+2, this.titleLabelY-2, 7629682, false);
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
        if (nm < 10)
        {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:HORSE_INVENTORY_LOCATION,x+10,yf,245.0F,4.0F,8,8,256,256);
        } else {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:HORSE_INVENTORY_LOCATION,x+6,yf,240.0F,16.0F,13,8,256,256);
        }
        guiGraphics.drawString(font,s, x + (nm>49&&nm<60?20:20) - 2 - font.width(s), y + 8, -9735563, false);
        guiGraphics.pose().popPose();
    }

}
