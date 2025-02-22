package com.dcqinv_mixins.Player.Screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.dcqinv.Content.PlayerGui.CustomSlots;
import com.dcqinv.Content.PlayerGui.CustomSlots.SlotWrap;
import com.dcqinv.Content.PlayerGui.IContainerScreen;
import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import net.minecraft.client.HotbarManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInvScreenMix extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> implements IGuiGraphics {
    public CreativeInvScreenMix(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory playerInventory, Component title) {super(menu, playerInventory, title);}

    @Shadow private static CreativeModeTab selectedTab; @Shadow private EditBox searchBox;
    @Shadow private float scrollOffs;@Shadow private boolean isCreativeSlot(@Nullable Slot slot) {return false;}
    @Shadow private void refreshSearchResults() {}@Shadow static final SimpleContainer CONTAINER = new SimpleContainer(45);
    @Shadow @Nullable private List<Slot> originalSlots;@Shadow @Nullable
    private Slot destroyItemSlot;@Shadow private boolean hasClickedOutside;
    @Shadow private int getTabX(CreativeModeTab tab) {return 0;}
    @Shadow private boolean canScroll() {return false;}

    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller_disabled");
    private static final ResourceLocation[] UNSELECTED_TOP_TABS = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_1"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_2"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_3"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_4"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_5"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_6"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_7")};
    private static final ResourceLocation[] SELECTED_TOP_TABS = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_1"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_2"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_3"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_4"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_5"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_6"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_7")};
    private static final ResourceLocation[] UNSELECTED_BOTTOM_TABS = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_1"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_2"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_3"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_4"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_5"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_6"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_7")};
    private static final ResourceLocation[] SELECTED_BOTTOM_TABS = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_1"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_2"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_3"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_4"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_5"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_6"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_7")};

    @Inject(method = "<init>(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/flag/FeatureFlagSet;Z)V",
            at = @At("TAIL")) public void setQ(LocalPlayer plr, FeatureFlagSet flagSet, boolean op, CallbackInfo info) {if (selectedTab != null)
    {if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {this.imageWidth = 215; this.imageHeight = 165; return;}}
        this.imageWidth = 212; this.imageHeight = 142;
    }

    @Override public void renderLabels(GuiGraphics p_283168_, int p_281774_, int p_281466_) {if (selectedTab.showTitle()) {RenderSystem.disableBlend();
            p_283168_.drawString(this.font, selectedTab.getDisplayName(), 8, 6, 7629682, false);}
    }

    @Overwrite
    public boolean insideScrollbar(double mouseX, double mouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        int k = i + 192;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + 112;
        return mouseX >= (double)k && mouseY >= (double)l && mouseX < (double)i1 && mouseY < (double)j1;
    }

    @Override
    public <T extends GuiEventListener & NarratableEntry> T addWidget(T listener) {
            if (listener instanceof EditBox && selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
                this.searchBox = new EditBox(this.font,((this.width-212)/2)+86,((this.height-142)/2)+6, 80, 9, Component.translatable("itemGroup.search"));
                this.searchBox.setMaxLength(50);
                this.searchBox.setBordered(false);
                this.searchBox.setVisible(false);
                this.searchBox.setTextColor(16777215);
                return super.addWidget((T)this.searchBox);
            }
            return super.addWidget(listener);
    }

    @Override public void renderBg(GuiGraphics p_282663_, float p_282504_, int p_282089_, int p_282249_) {
        Iterator<CreativeModeTab> var5 = CreativeModeTabs.tabs().iterator();

        while(var5.hasNext()) {
            CreativeModeTab creativeModeTab = var5.next();
            if (creativeModeTab != selectedTab) {
                this.renderTabButton(p_282663_, creativeModeTab);
            }
        }

        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
            this.imageWidth = 215; this.imageHeight = 165;
            this.topPos = ((this.height - this.imageHeight) / 2)-11;
            this.leftPos = ((this.width - this.imageWidth) / 2)-2;
        } else {
            this.imageWidth = 212; this.imageHeight = 142;
            this.topPos = (this.height - this.imageHeight) / 2;
            this.leftPos = (this.width - this.imageWidth) / 2;
        }
        p_282663_.blit(
                RenderType::guiTextured, selectedTab.getBackgroundTexture(), this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256
        );
        this.searchBox.render(p_282663_, p_282089_, p_282249_, p_282504_);
        int j = this.leftPos + 192;
        int k = this.topPos + 18;
        int i = k + 112;
        if (selectedTab.canScroll()) {
            ResourceLocation resourcelocation = this.canScroll() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
            p_282663_.blitSprite(RenderType::guiTextured, resourcelocation, j, k + (int)((float)(i - k - 17) * this.scrollOffs), 12, 15);
        }

        this.renderTabButton(p_282663_, selectedTab);
        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    p_282663_,
                    this.leftPos + 89,
                    this.topPos + 8,
                    this.leftPos + 123,
                    this.topPos + 52,
                    20,
                    0.0625F,
                    (float)p_282089_,
                    (float)p_282249_,
                    this.minecraft.player
            );
        }
    }

    @Overwrite
    public void renderTabButton(GuiGraphics guiGraphics, CreativeModeTab creativeModeTab) {
        boolean flag = creativeModeTab == selectedTab;
        boolean flag1 = creativeModeTab.row() == CreativeModeTab.Row.TOP;
        int i = creativeModeTab.column();
        int j = this.leftPos + this.getTabX(creativeModeTab);
        int k = this.topPos - (flag1 ? 28 : -(this.imageHeight - 4));
        ResourceLocation[] aresourcelocation;
        if (flag1) {
            aresourcelocation = flag ? SELECTED_TOP_TABS : UNSELECTED_TOP_TABS;
        } else { if (flag) { aresourcelocation = SELECTED_BOTTOM_TABS; k -= 2;
        } else { aresourcelocation = UNSELECTED_BOTTOM_TABS; }
        }
        guiGraphics.blitSprite(RenderType::guiTextured, aresourcelocation[Mth.clamp(i, 0, aresourcelocation.length)], j, k, 26, 32);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
        j += 5;
        k += 8 + (flag1 ? 1 : -1);
        ItemStack itemstack = creativeModeTab.getIconItem();
        guiGraphics.renderItem(itemstack, j, k);
        guiGraphics.renderItemDecorations(this.font, itemstack, j, k);
        guiGraphics.pose().popPose();
    }

    @Override
    public void renderItemCountBg(GuiGraphics guiGraphics, int x, int y,ItemStack stack, String countString,boolean plrInv,String title) { int nm = stack.getCount();
        if (!(nm > 1)) { if (countString != null) { nm = Integer.parseInt(countString); } else { return; } }
        String s = countString == null ? String.valueOf(nm) : countString; s = s.replace("9","₉");
        s = s.replace("0","₀"); s = s.replace("1","₁"); s = s.replace("2","₂");
        s = s.replace("3","₃"); s = s.replace("4","₄"); s = s.replace("5","₅");
        s = s.replace("6","₆"); s = s.replace("7","₇"); s = s.replace("8","₈");
        int txtcolor = plrInv?-9735563:-1;
        guiGraphics.pose().pushPose(); int xf =x+(nm>9?6:10); int yf =y+10;
        guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
        if (nm < 10) {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:selectedTab.getBackgroundTexture(),x+10,yf,245.0F,4.0F,8,8,256,256);
        } else {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:selectedTab.getBackgroundTexture(),x+6,yf,240.0F,16.0F,13,8,256,256);
        }
        guiGraphics.drawString(font,s, x + (nm>49&&nm<60?20:20) - 2 - font.width(s), y + 8, txtcolor, !plrInv);
        guiGraphics.pose().popPose();
    }

    @Overwrite
    private void selectTab(CreativeModeTab tab) {
        CreativeModeTab creativemodetab = selectedTab;
        selectedTab = tab;
        this.quickCraftSlots.clear();
        ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.clear();
        this.clearDraggingState();
        int k;
        int i1;
        if (selectedTab.getType() == CreativeModeTab.Type.HOTBAR) {
            HotbarManager hotbarmanager = this.minecraft.getHotbarManager();
            for(k = 0; k < 9; ++k) {
                Hotbar hotbar = hotbarmanager.get(k);
                if (hotbar.isEmpty()) {
                    for(i1 = 0; i1 < 9; ++i1) {
                        if (i1 == k) {
                            ItemStack itemstack = new ItemStack(Items.PAPER);
                            itemstack.set(DataComponents.CREATIVE_SLOT_LOCK, Unit.INSTANCE);
                            Component component = this.minecraft.options.keyHotbarSlots[k].getTranslatedKeyMessage();
                            Component component1 = this.minecraft.options.keySaveHotbarActivator.getTranslatedKeyMessage();
                            itemstack.set(DataComponents.ITEM_NAME, Component.translatable("inventory.hotbarInfo", new Object[]{component1, component}));
                            ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.add(itemstack);
                        } else {
                            ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.add(ItemStack.EMPTY);
                        }
                    }
                } else {
                    ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.addAll(hotbar.load(this.minecraft.level.registryAccess()));
                }
            }
        } else if (selectedTab.getType() == CreativeModeTab.Type.CATEGORY) {
            ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.addAll(selectedTab.getDisplayItems());
        }
        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
            AbstractContainerMenu abstractcontainermenu = this.minecraft.player.inventoryMenu;
            if (this.originalSlots == null) {
                this.originalSlots = ImmutableList.copyOf(((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots);
            }
            ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.clear();
            int slotI = 0;
            for (;slotI < 5;++slotI) {
                ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.add(
                        new CustomSlots().new SlotWrap((Slot)abstractcontainermenu.slots.get(slotI), slotI,-2000,-2000));
            } int x=8; int y=58;
            for (int rw = 0;rw < 2;++rw) {
                for (int rr = 0;rr < 2;++rr,++slotI) {
                    Slot slot = new CustomSlots().new SlotWrap((Slot)abstractcontainermenu.slots.get(slotI), slotI,71+rr*56,8+rw*28);
                    ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.add(slot);
                }
            }
            ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.add(new CustomSlots().new SlotWrap((Slot)abstractcontainermenu.slots.get(slotI),slotI,x,y));
            slotI += 1;
            for (int r = 1; r < 10; ++r,++slotI) {
                ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.add(
                        new CustomSlots().new SlotWrap((Slot)abstractcontainermenu.slots.get(slotI),slotI,(x+2+(r*2))+r*18,y));
            }
            for (int jk = 1; jk < 4; ++jk) { for (int r = 0; r < 10; ++r,++slotI) {
                ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.add(
                        new CustomSlots().new SlotWrap((Slot)abstractcontainermenu.slots.get(slotI),slotI,((r>0?(x+2):x)+(r*2))+r*18,(y+3+jk)+jk*18));
            }}
            ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.add(
                    new CustomSlots().new SlotWrap((Slot)abstractcontainermenu.slots.get(slotI),slotI,x,y+82));
            slotI += 1;
            for(int r = 0; r < 9; ++r,++slotI) {
                ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.add(
                        new CustomSlots().new SlotWrap((Slot)abstractcontainermenu.slots.get(slotI),slotI,((x+22+(r*2))+r*18),y+82));
            }
            this.destroyItemSlot = new Slot(CONTAINER, 0, 190, 36);
            ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.add(this.destroyItemSlot);
        } else if (creativemodetab.getType() == CreativeModeTab.Type.INVENTORY) {
            ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.clear();
            ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.addAll(this.originalSlots);
            this.originalSlots = null;
        }
        if (selectedTab.getType() == CreativeModeTab.Type.SEARCH) {
            this.searchBox.setVisible(true);
            this.searchBox.setCanLoseFocus(false);
            this.searchBox.setFocused(true);
            if (creativemodetab != tab) {
                this.searchBox.setValue("");
            }
            //this.searchBox.setWidth(selectedTab.getSearchBarWidth());
            this.searchBox.setX(((this.width-212)/2) + 180 - this.searchBox.getWidth());
            this.refreshSearchResults();
        } else {
            this.searchBox.setVisible(false);
            this.searchBox.setCanLoseFocus(true);
            this.searchBox.setFocused(false);
            this.searchBox.setValue("");
        }
        this.scrollOffs = 0.0F;
        ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).scrollTo(0.0F);
    }

    private void onMouseClickAction(@Nullable Slot slot, ClickType type) {
        if (slot != null && slot.hasItem()) {
            Iterator var3 = ((IContainerScreen) this).getActions().iterator();
            while(var3.hasNext()) {
                ItemSlotMouseAction itemslotmouseaction = (ItemSlotMouseAction)var3.next();
                if (itemslotmouseaction.matches(slot)) {
                    itemslotmouseaction.onSlotClicked(slot, type);
                }
            }
        }
    }

    @Overwrite
    public void slotClicked(@Nullable Slot slot, int slotId, int mouseButton, ClickType type) {
        if (isCreativeSlot(slot)) { searchBox.moveCursorToEnd(false); searchBox.setHighlightPos(0);}
        boolean quickmove = type == ClickType.QUICK_MOVE; type = slotId == -999 && type == ClickType.PICKUP ? ClickType.THROW : type;
        if (type == ClickType.THROW && !this.minecraft.player.canDropItems()) {return;} this.onMouseClickAction(slot, type);

        if (slot == null && selectedTab.getType() != CreativeModeTab.Type.INVENTORY && type != ClickType.QUICK_CRAFT) { // >>>
            if (!this.menu.getCarried().isEmpty() && hasClickedOutside) { if (!this.minecraft.player.canDropItems()) {return;} if (mouseButton == 0) {
                        this.minecraft.player.drop(this.menu.getCarried(), true);
                        this.minecraft.gameMode.handleCreativeModeItemDrop(this.menu.getCarried());
                        this.menu.setCarried(ItemStack.EMPTY); } if (mouseButton == 1) {
                        ItemStack itemstack5 = this.menu.getCarried().split(1);
                        this.minecraft.player.drop(itemstack5, true);
                        this.minecraft.gameMode.handleCreativeModeItemDrop(itemstack5); } } return;
        } if (slot != null && !slot.mayPickup(this.minecraft.player)) {return;}

        if (slot == destroyItemSlot && quickmove) { // >>>
            for (int i = 0; i < this.minecraft.player.inventoryMenu.getItems().size(); i++) {
                this.minecraft.player.inventoryMenu.getSlot(i).set(ItemStack.EMPTY);
                this.minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, i); }
        } else if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) { // >>>
            if (slot == destroyItemSlot) {
                this.menu.setCarried(ItemStack.EMPTY);
            } else if (type == ClickType.THROW && slot != null && slot.hasItem()) { // ???
                ItemStack itemstack = slot.remove(mouseButton == 0 ? 1 : slot.getItem().getMaxStackSize());
                ItemStack itemstack1 = slot.getItem();
                this.minecraft.player.drop(itemstack, true);
                this.minecraft.gameMode.handleCreativeModeItemDrop(itemstack);
                this.minecraft.gameMode.handleCreativeModeItemAdd(itemstack1,((SlotWrap)slot).target.index);
            } else if (type == ClickType.THROW && slotId == -999 && !this.menu.getCarried().isEmpty()) { // ???
                this.minecraft.player.drop(this.menu.getCarried(), true);
                this.minecraft.gameMode.handleCreativeModeItemDrop(this.menu.getCarried());
                this.menu.setCarried(ItemStack.EMPTY);
            } else { // ???
                this.minecraft.player.inventoryMenu
                        .clicked(slot == null ? slotId : ((SlotWrap)slot).target.index, mouseButton, type, this.minecraft.player);
                this.minecraft.player.inventoryMenu.broadcastChanges(); }
        } else if (type != ClickType.QUICK_CRAFT && slot.container == CONTAINER) { // ???
            ItemStack itemstack4 = this.menu.getCarried();
            ItemStack itemstack6 = slot.getItem();
            if (type == ClickType.SWAP) {
                if (!itemstack6.isEmpty()) {
                    this.minecraft.player.getInventory().setItem(mouseButton, itemstack6.copyWithCount(itemstack6.getMaxStackSize()));
                    this.minecraft.player.inventoryMenu.broadcastChanges();
                } return; }
            if (type == ClickType.CLONE) {
                if (this.menu.getCarried().isEmpty() && slot.hasItem()) {
                    ItemStack itemstack8 = slot.getItem();
                    this.menu.setCarried(itemstack8.copyWithCount(itemstack8.getMaxStackSize()));
                } return; }
            if (type == ClickType.THROW) {
                if (!itemstack6.isEmpty()) {
                    ItemStack itemstack7 = itemstack6.copyWithCount(mouseButton == 0 ? 1 : itemstack6.getMaxStackSize());
                    this.minecraft.player.drop(itemstack7, true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop(itemstack7);
                } return; }
            if (!itemstack4.isEmpty() && !itemstack6.isEmpty() && ItemStack.isSameItemSameComponents(itemstack4, itemstack6)) {
                if (mouseButton == 0) {if (quickmove) {
                    itemstack4.setCount(itemstack4.getMaxStackSize());
                } else if (itemstack4.getCount() < itemstack4.getMaxStackSize()) {
                    itemstack4.grow(1);
                }
                } else {
                    itemstack4.shrink(1);
                }
            } else if (!itemstack6.isEmpty() && itemstack4.isEmpty()) {
                int l = quickmove ? itemstack6.getMaxStackSize() : itemstack6.getCount();
                this.menu.setCarried(itemstack6.copyWithCount(l));
            } else if (mouseButton == 0) {
                this.menu.setCarried(ItemStack.EMPTY);
            } else if (!this.menu.getCarried().isEmpty()) {
                this.menu.getCarried().shrink(1);
            }
        } else if (this.menu != null) {
            ItemStack itemstack3 = slot == null ? ItemStack.EMPTY : this.menu.getSlot(slot.index).getItem();
            this.menu.clicked(slot == null ? slotId : slot.index, mouseButton, type, this.minecraft.player);
            if (AbstractContainerMenu.getQuickcraftHeader(mouseButton) == 2) {
                for (int j = 0; j < 9; j++) {
                    this.minecraft.gameMode.handleCreativeModeItemAdd(this.menu.getSlot(45 + j).getItem(), 50 + j);
                }
            } else if (slot != null
                    && Inventory.isHotbarSlot(slot.getContainerSlot())
                    && selectedTab.getType() != CreativeModeTab.Type.INVENTORY) {
                if (type == ClickType.THROW && !itemstack3.isEmpty() && !this.menu.getCarried().isEmpty()) {
                    int k = mouseButton == 0 ? 1 : itemstack3.getCount();
                    ItemStack itemstack2 = itemstack3.copyWithCount(k);
                    itemstack3.shrink(k);
                    this.minecraft.player.drop(itemstack2, true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop(itemstack2);
                }
                this.minecraft.player.inventoryMenu.broadcastChanges(); } }
    }
}
