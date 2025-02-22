package com.dcqinv_mixins.Player.Screens.Abstractions;

import com.dcqinv.Content.PlayerGui.IContainerScreen;
import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerScreenMix<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T>, IContainerScreen {
    @Shadow protected int imageWidth = 215; @Shadow protected int imageHeight = 194;
    @Shadow @Nullable protected Slot hoveredSlot; @Shadow private ItemStack draggingItem;
    private static final ResourceLocation SLOT_HIGHLIGHT_BACK_SPRITE = ResourceLocation.withDefaultNamespace("container/slot_highlight_back");
    private static final ResourceLocation SLOT_HIGHLIGHT_FRONT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot_highlight_front");
    @Shadow private final List<ItemSlotMouseAction> itemSlotMouseActions;
    @Shadow protected final Component playerInventoryTitle;
    @Shadow protected int titleLabelX;
    @Shadow protected int titleLabelY;

    protected ContainerScreenMix(Component title) {
        super(title);
        playerInventoryTitle = null;
        itemSlotMouseActions = null;
    }

    @Override
    public List<ItemSlotMouseAction> getActions() {
        return this.itemSlotMouseActions;
    }

    @Overwrite
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 7629682, false);
        //guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 115, 4210752, false);
    }

    @Overwrite
    private void renderFloatingItem(GuiGraphics guiGraphics, ItemStack stack, int x, int y, @Nullable String text) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 232.0F);
        guiGraphics.renderItem(stack, x, y);
        var font = net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.of(stack).getFont(stack, net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.FontContext.ITEM_COUNT);
        guiGraphics.renderItemDecorations(font == null ? this.font : font, stack, x, y - (this.draggingItem.isEmpty() ? 0 : 8), text);
        if (stack.getCount() != 1 || text != null) {
            String s = text == null ? String.valueOf(stack.getCount()) : text;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);

            s = s.replace("9","₉");
            s = s.replace("0","₀"); s = s.replace("1","₁"); s = s.replace("2","₂");
            s = s.replace("3","₃"); s = s.replace("4","₄"); s = s.replace("5","₅");
            s = s.replace("6","₆"); s = s.replace("7","₇"); s = s.replace("8","₈");
            guiGraphics.drawString(font == null ? this.font : font, (String)s, x + 19 - 2 - (font == null ? this.font : font).width(s), y + 6 + 3, -1, true);
            guiGraphics.pose().popPose();
        }
        guiGraphics.pose().popPose();
    }

    @Overwrite
    protected void renderSlotContents(GuiGraphics guiGraphics, ItemStack stack, Slot slot, @org.jetbrains.annotations.Nullable String countString) {
        int i = slot.x; int j = slot.y; int j1 = slot.x + slot.y * this.imageWidth;
        if (slot.isFake()) {guiGraphics.renderFakeItem(stack, i, j, j1);} else {guiGraphics.renderItem(stack, i, j, j1);}
        guiGraphics.renderItemDecorations(this.font, stack, i, j, countString);
        if (this instanceof IGuiGraphics igui) {
            igui.renderItemCountBg(guiGraphics,i,j,stack,countString,slot.container instanceof Inventory,this.title.toString());
        }
    }
}