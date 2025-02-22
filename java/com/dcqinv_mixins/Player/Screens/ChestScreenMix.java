package com.dcqinv_mixins.Player.Screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import com.dcqinv.Content.world.Mobs.IPlayerGui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.ClientHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(ContainerScreen.class)
public abstract class ChestScreenMix extends AbstractContainerScreen<ChestMenu> implements IGuiGraphics {

    @Shadow private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    @Shadow private final int containerRows;

    private IPlayerGui iPlayer; private boolean displayName; private String containerName;
    private int receivedID = -1; private final ItemStack DefaultIconStack = Items.ITEM_FRAME.getDefaultInstance();
    private boolean displayOptions; private int selected_ = -1; private boolean top; private ItemStack Iconstack = this.DefaultIconStack;
    private static final ResourceLocation BARREL_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/barrel.png");
    private static final ResourceLocation ENDER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/ender_chest.png");

    @Override protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

    @Inject(method = "<init>",at = @At("TAIL"))
    public void ini(ChestMenu menu, Inventory playerInventory, Component title, CallbackInfo info) {
        this.iPlayer = (IPlayerGui) playerInventory.player;
        this.setContainer();
    }

    public ChestScreenMix(ChestMenu menu, Inventory playerInventory, Component title,int Rows) {
        super(menu, playerInventory, title);
        this.containerRows = Rows;
    }

    private void setContainer() {
        this.receivedID = this.iPlayer.getContainerTab();
        this.containerName = this.iPlayer.getContainerName();
        this.displayName = !this.containerName.isEmpty(); this.selected_ = this.receivedID;
        if (this.selected_ > -1) { this.Iconstack = BuiltInRegistries.CREATIVE_MODE_TAB.get(this.selected_).get().value().getIconItem();}
    }

    @Overwrite
    protected void renderBg(GuiGraphics gui, float p_282334_, int p_282603_, int p_282158_) { String stitle = this.title.toString();
        this.topPos = ((this.height - this.imageHeight) / 2)-10;
        int i = (this.width - this.imageWidth) / 2; boolean chestType = this.containerRows>4;
        int j = ((this.height - this.imageHeight) / 2)-10;
        ResourceLocation resourceLocation = stitle.contains("container.barrel") ? BARREL_BACKGROUND :
                stitle.contains("container.enderchest") ? ENDER_BACKGROUND : CONTAINER_BACKGROUND;
        gui.blit(RenderType::guiTextured, resourceLocation,i, j, 0.0F, 0.0F, this.imageWidth, this.containerRows * 18 + 17, 256, 256);
        gui.blit(RenderType::guiTextured, resourceLocation,i,j + this.containerRows * 18 + (chestType?17:10), 0.0F,chestType?126.0F:121.0F, this.imageWidth, 126, 256, 256);
        if (this.displayOptions) {
            gui.renderFakeItem(Iconstack,this.leftPos+9,this.top?this.topPos+13:this.topPos+71);
            gui.renderItemDecorations(this.font,Iconstack,this.leftPos+10,this.top?this.topPos+13:this.topPos+71);
        }
        if (this.displayName) {
            /*int ctab = this.iPlayer.getContainerTab(); String cname = this.iPlayer.getContainerName();
            if (cname != this.containerName || ctab != this.receivedID) { this.setContainer(); }*/
            boolean hasprefix = this.containerName.startsWith("!");
            String sname = hasprefix?this.containerName.split("!")[1]:this.containerName;
            int fw = font.width(sname); int total = 5+fw+7; boolean toobig = total>= 203;
            if (toobig && total < 217) { fw = 205; total = 217; }
            gui.blit(RenderType::guiTextured,resourceLocation,i,j-13,223.0F,39.0F,5,18,256,256);
            gui.blit(RenderType::guiTextured,resourceLocation,i+5,j-13,228.0F,39.0F,fw+7,18,17,18,256,256);
            if (toobig) {
                gui.blit(RenderType::guiTextured,resourceLocation,i+total,j-13,245.0F,58.0F,5,23,256,256);
                gui.blit(RenderType::guiTextured,resourceLocation,i+210,j+5,223.0F,76.0F,5,5,256,256);
                gui.blit(RenderType::guiTextured,resourceLocation,i+215,j+5,228.0F,76.0F,total-215,5,5,5,256,256);
            } else {
                gui.blit(RenderType::guiTextured, resourceLocation,i+total,j-13, 245.0F, 39.0F, 5, 18, 256, 256);
            }
            gui.drawString(this.font,sname,i+8, j-6, 4210752, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = this.leftPos + 11; int j = this.topPos + 31;
        if (button == 0 && this.containerRows > 3 && mouseX >= (double)i && mouseX < (double)(i+13) && mouseY >= (double)j+58 && mouseY < (double)(j+68)) {
            if (!(this.displayOptions && this.top)) {this.displayOptions = !this.displayOptions;} this.top = false;
        } else if (button == 0 && mouseX >= (double)i && mouseX < (double)(i+13) && mouseY >= (double)j && mouseY < (double)(j + 10)) {
            if (!(this.displayOptions && !this.top)) {this.displayOptions = !this.displayOptions;} this.top = true;
        } else if (this.displayOptions) { if (!this.top) {j +=58;}
            if (button == 0 && mouseX >= (double)i-2 && mouseX < (double)(i+14) && mouseY >= (double)j-18 && mouseY < (double)(j-2) ) { this.selected_ += 1;
                if (BuiltInRegistries.CREATIVE_MODE_TAB.get(this.selected_).isEmpty()) { this.selected_ = -1; this.Iconstack = this.DefaultIconStack;
                } else {
                    while (true) {
                        Optional<Holder.Reference<CreativeModeTab>> tabw = BuiltInRegistries.CREATIVE_MODE_TAB.get(this.selected_);
                        if (tabw.isEmpty()) {this.selected_ = -1; this.Iconstack = this.DefaultIconStack;break;}
                        if (tabw.get().is(CreativeModeTabs.HOTBAR)) {this.selected_+=1;continue;}
                        if (tabw.get().is(CreativeModeTabs.SEARCH)) {this.selected_+=1;continue;}
                        if (tabw.get().is(CreativeModeTabs.OP_BLOCKS)) {this.selected_+=1;continue;}
                        if (tabw.get().is(CreativeModeTabs.INVENTORY)) {this.selected_+=1;continue;}
                        break;
                    }
                    if (this.selected_ > -1) { this.Iconstack =  BuiltInRegistries.CREATIVE_MODE_TAB.get(this.selected_).get().value().getIconItem(); }
                }
            } /*else if ((button == 0 || button == 2) && mouseX >= (double)i-5 && mouseX < (double)(i+5) && mouseY >= (double)j+12 && mouseY < (double)(j+21)) {
                if (this.selected_ > -1 && button == 0) {
                    //move selected stacks
                } else {
                    for (int i1 = 0; i1 < player.inventoryMenu.slots.size()-1;i1++) {
                        if (player.inventoryMenu.slots.get(i1).hasItem()) {
                            getMenu().quickMoveStack(player,76);
                        }
                    }
                }
            } else if ((button == 0 || button == 2) && mouseX >= (double)i+7 && mouseX < (double)(i+17) && mouseY >= (double)j+12 && mouseY < (double)(j+21)) {
                if (this.selected_ > -1 && button == 0) {
                    //move selected stacks
                } else {
                    for (int i1 = 0; i1 < getMenu().slots.size()-1;i1++) {
                        getMenu().quickMoveStack(player,i1);
                    }
                }
            }*/
        }
        return super.mouseClicked(mouseX, mouseY, button);

    }

    @Override
    public void renderItemCountBg(GuiGraphics guiGraphics, int x, int y,ItemStack stack, String countString,boolean plrInv,String title) { int nm = stack.getCount();
        if (this.selected_ > -1 && BuiltInRegistries.CREATIVE_MODE_TAB.get(this.selected_).get().value().contains(stack.getItem().getDefaultInstance())) {
            final float oversize = 1.1F; final float centerX = x + 8.0F; final float centerY = y + 8.0F;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(centerX, centerY, 0.0F);
            guiGraphics.pose().scale(oversize, oversize, 1.0F);
            guiGraphics.pose().translate(-centerX, -centerY, -10.0F);
            if (stack.getItemName().toString().contains("shulker_box")
                    || stack.getItemName().toString().contains("wool"))
            { RenderSystem.setShaderColor(3.0f, 3.0f, 3.0f, 1.0f);
            } else { RenderSystem.setShaderColor(100.0f, 100.0f, 100.0f, 1.0f); }
            guiGraphics.renderItem(stack, x, y);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            guiGraphics.pose().popPose();
        }
        if (!(nm > 1)) { if (countString != null) { nm = Integer.parseInt(countString); } else { return; } }
        String s = countString == null ? String.valueOf(nm) : countString; s = s.replace("9","₉");
        s = s.replace("0","₀"); s = s.replace("1","₁"); s = s.replace("2","₂");
        s = s.replace("3","₃"); s = s.replace("4","₄"); s = s.replace("5","₅");
        s = s.replace("6","₆"); s = s.replace("7","₇"); s = s.replace("8","₈");
        int txtcolor = plrInv?-9735563:-1;
        guiGraphics.pose().pushPose(); int xf =x+(nm>9?6:10); int yf =y+10;
        guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
        List<ClientTooltipComponent> list = ClientHooks.gatherTooltipComponents(stack, Screen.getTooltipFromItem(this.minecraft, stack), stack.getTooltipImage(), 10, 100, 100, font);
        if (nm < 10) {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:title.contains("container.barrel")?BARREL_BACKGROUND:
                    title.contains("container.enderchest")?ENDER_BACKGROUND:CONTAINER_BACKGROUND,x+10,yf,245.0F,4.0F,8,8,256,256);
        } else {
            guiGraphics.blit(RenderType::guiTextured,plrInv?INVENTORY_LOCATION:title.contains("container.barrel")?BARREL_BACKGROUND:
                    title.contains("container.enderchest")?ENDER_BACKGROUND:CONTAINER_BACKGROUND,x+6,yf,240.0F,16.0F,13,8,256,256);
        }
        guiGraphics.drawString(font,s, x + (nm>49&&nm<60?20:20) - 2 - font.width(s), y + 8, txtcolor, !plrInv);
        guiGraphics.pose().popPose();
    }
}
