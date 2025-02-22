package com.dcqinv_mixins.Player.Screens.Abstractions;

import com.dcqinv.Content.PlayerGui.IContainerScreen;
import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import com.google.common.collect.Sets;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Set;

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerScreenMix<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T>, IContainerScreen {
    @Shadow public int imageWidth = 215; @Shadow public int imageHeight = 194;
    @Shadow @Nullable
    public Slot hoveredSlot; @Shadow private ItemStack draggingItem;
    private static final ResourceLocation SLOT_HIGHLIGHT_BACK_SPRITE = ResourceLocation.withDefaultNamespace("container/slot_highlight_back");
    private static final ResourceLocation SLOT_HIGHLIGHT_FRONT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot_highlight_front");
    @Shadow private final List<ItemSlotMouseAction> itemSlotMouseActions;
    @Shadow public final Component playerInventoryTitle;
    @Shadow public int titleLabelX;
    @Shadow public int titleLabelY;
    @Shadow @Nullable private Slot clickedSlot;
    @Shadow private boolean isSplittingStack;
    @Shadow public boolean isQuickCrafting;
    @Shadow public final Set<Slot> quickCraftSlots = Sets.newHashSet();
    @Shadow private int quickCraftingType;
    @Shadow private void recalculateQuickCraftRemaining() {}

    public ContainerScreenMix(Component title) {
        super(title);
        playerInventoryTitle = null;
        itemSlotMouseActions = null;
    }

    @Override
    public List<ItemSlotMouseAction> getActions() {
        return this.itemSlotMouseActions;
    }

    @Overwrite
    public void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 7629682, false);
        //guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 115, 4210752, false);
    }

    @Overwrite
    private void renderFloatingItem(GuiGraphics guiGraphics, ItemStack stack, int x, int y, @Nullable String text) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 232.0F);
        guiGraphics.renderItem(stack, x, y);
        guiGraphics.renderItemDecorations(this.font, stack, x, y - (this.draggingItem.isEmpty() ? 0 : 8), text);
        if (stack.getCount() != 1 || text != null) {
            String s = text == null ? String.valueOf(stack.getCount()) : text;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);

            s = s.replace("9","₉");
            s = s.replace("0","₀"); s = s.replace("1","₁"); s = s.replace("2","₂");
            s = s.replace("3","₃"); s = s.replace("4","₄"); s = s.replace("5","₅");
            s = s.replace("6","₆"); s = s.replace("7","₇"); s = s.replace("8","₈");
            guiGraphics.drawString(this.font, (String)s, x + 19 - 2 - (this.font).width(s), y + 6 + 3, -1, true);
            guiGraphics.pose().popPose();
        }
        guiGraphics.pose().popPose();
    }

    @Overwrite
    public void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        int i = slot.x;
        int j = slot.y;
        ItemStack itemStack = slot.getItem();
        boolean bl = false;
        boolean bl2 = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
        ItemStack itemStack2 = this.getMenu().getCarried();
        String string = null;
        int k;
        if (slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemStack.isEmpty()) {
            itemStack = itemStack.copyWithCount(itemStack.getCount() / 2);
        } else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !itemStack2.isEmpty()) {
            if (this.quickCraftSlots.size() == 1) {
                return;
            }

            if (AbstractContainerMenu.canItemQuickReplace(slot, itemStack2, true) && this.getMenu().canDragTo(slot)) {
                bl = true;
                k = Math.min(itemStack2.getMaxStackSize(), slot.getMaxStackSize(itemStack2));
                int l = slot.getItem().isEmpty() ? 0 : slot.getItem().getCount();
                int m = AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots, this.quickCraftingType, itemStack2) + l;
                if (m > k) {
                    m = k;
                    String var10000 = ChatFormatting.YELLOW.toString();
                    string = var10000 + k;
                }

                itemStack = itemStack2.copyWithCount(m);
            } else {
                this.quickCraftSlots.remove(slot);
                this.recalculateQuickCraftRemaining();
            }
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
        if (itemStack.isEmpty() && slot.isActive()) {
            ResourceLocation resourceLocation = slot.getNoItemIcon();
            if (resourceLocation != null) {
                guiGraphics.blitSprite(RenderType::guiTextured, resourceLocation, i, j, 16, 16);
                bl2 = true;
            }
        }

        if (!bl2) {
            if (bl) {
                guiGraphics.fill(i, j, i + 16, j + 16, -2130706433);
            }

            int j1 = slot.x + slot.y * this.imageWidth;
            if (slot.isFake()) {guiGraphics.renderFakeItem(itemStack, i, j, j1);} else {guiGraphics.renderItem(itemStack, i, j, j1);}
            guiGraphics.renderItemDecorations(this.font, itemStack, i, j, string);
            if (this instanceof IGuiGraphics igui) {
                igui.renderItemCountBg(guiGraphics,i,j,itemStack,string,slot.container instanceof Inventory,this.title.toString());
            }
        }

        guiGraphics.pose().popPose();
    }
}