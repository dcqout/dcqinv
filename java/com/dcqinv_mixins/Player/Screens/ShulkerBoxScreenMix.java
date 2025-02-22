package com.dcqinv_mixins.Player.Screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.dcqinv.Content.PlayerGui.IGuiGraphics;
import com.dcqinv.Content.world.Mobs.IPlayerGui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ShulkerBoxScreen.class)
public abstract class ShulkerBoxScreenMix extends AbstractContainerScreen<ShulkerBoxMenu> implements IGuiGraphics {
    private IPlayerGui iPlayer; private boolean displayName; private String containerName;
    private int receivedID = -1; private final ItemStack DefaultIconStack = Items.ITEM_FRAME.getDefaultInstance();
    private boolean displayOptions; private int selected_ = -1; private ItemStack Iconstack = this.DefaultIconStack;
    private static final String location = "textures/gui/container/shulker_boxes/";
    private static final ResourceLocation CONTAINER_TEXTURE = ResourceLocation.withDefaultNamespace(location+"shulker_box.png");
    private static final ResourceLocation CONTAINER_TEXTURE_WHITE = ResourceLocation.withDefaultNamespace(location+"white_shulker_box.png");
    private static final ResourceLocation CONTAINER_TEXTURE_ORANGE = ResourceLocation.withDefaultNamespace(location+"orange_shulker_box.png");
    private static final ResourceLocation CONTAINER_TEXTURE_MAGENTA = ResourceLocation.withDefaultNamespace(location+"magenta_shulker_box.png");
    private static final ResourceLocation CONTAINER_TEXTURE_LIGHT_BLUE = ResourceLocation.withDefaultNamespace(location+"light_blue_shulker_box.png");
    private static final ResourceLocation CONTAINER_TEXTURE_YELLOW = ResourceLocation.withDefaultNamespace(location+"yellow_shulker_box.png");
    private static final ResourceLocation CONTAINER_TEXTURE_LIME = ResourceLocation.withDefaultNamespace(location+"lime_shulker_box.png");
    private static final ResourceLocation CONTAINER_TEXTURE_PINK = ResourceLocation.withDefaultNamespace(location+"pink_shulker_box.png");
    private static final ResourceLocation CONTAINER_TEXTURE_GRAY = ResourceLocation.withDefaultNamespace(location+"gray_shulker_box.png");
    private static final ResourceLocation CONTAINER_TEXTURE_LIGHT_GRAY = ResourceLocation.withDefaultNamespace(location+"light_gray_shulker_box.png");
    private static final ResourceLocation CONTAINER_TEXTURE_CYAN = ResourceLocation.withDefaultNamespace(location+"cyan_shulker_box.png");
    private static final ResourceLocation CONTAINER_TEXTURE_BLUE = ResourceLocation.withDefaultNamespace(location+"blue_shulker_box.png");
    private static final ResourceLocation CONTAINER_TEXTURE_BROWN = ResourceLocation.withDefaultNamespace(location+"brown_shulker_box.png");
    private static final ResourceLocation CONTAINER_TEXTURE_GREEN = ResourceLocation.withDefaultNamespace(location+"green_shulker_box.png");
    private static final ResourceLocation CONTAINER_TEXTURE_RED = ResourceLocation.withDefaultNamespace(location+"red_shulker_box.png");
    private static final ResourceLocation CONTAINER_TEXTURE_BLACK = ResourceLocation.withDefaultNamespace(location+"black_shulker_box.png");
    private static final ResourceLocation CONTAINER_TEXTURE_PURPLE = ResourceLocation.withDefaultNamespace(location+"purple_shulker_box.png");

    private ResourceLocation containerColor;

    @Inject(method = "<init>",at = @At("TAIL"))
    public void ini(ShulkerBoxMenu menu,Inventory playerInventory,Component title,CallbackInfo info) {
        this.iPlayer = (IPlayerGui) playerInventory.player;
        this.setContainer();
    }

    public ShulkerBoxScreenMix(ShulkerBoxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    private void setContainer() {
        this.containerColor = switch (this.iPlayer.getShulker()) {
            case null -> CONTAINER_TEXTURE;
            case WHITE -> CONTAINER_TEXTURE_WHITE;
            case ORANGE -> CONTAINER_TEXTURE_ORANGE;
            case MAGENTA -> CONTAINER_TEXTURE_MAGENTA;
            case LIGHT_BLUE -> CONTAINER_TEXTURE_LIGHT_BLUE;
            case YELLOW -> CONTAINER_TEXTURE_YELLOW;
            case LIME -> CONTAINER_TEXTURE_LIME;
            case PINK -> CONTAINER_TEXTURE_PINK;
            case GRAY -> CONTAINER_TEXTURE_GRAY;
            case LIGHT_GRAY -> CONTAINER_TEXTURE_LIGHT_GRAY;
            case CYAN -> CONTAINER_TEXTURE_CYAN;
            case BLUE -> CONTAINER_TEXTURE_BLUE;
            case BROWN -> CONTAINER_TEXTURE_BROWN;
            case GREEN -> CONTAINER_TEXTURE_GREEN;
            case RED -> CONTAINER_TEXTURE_RED;
            case BLACK -> CONTAINER_TEXTURE_BLACK;
            case PURPLE -> CONTAINER_TEXTURE_PURPLE;
        };
        this.receivedID = this.iPlayer.getContainerTab();
        this.containerName = this.iPlayer.getContainerName();
        this.displayName = !this.containerName.isEmpty(); this.selected_ = this.receivedID;
        if (this.selected_ > -1) { this.Iconstack = BuiltInRegistries.CREATIVE_MODE_TAB.get(this.selected_).get().value().getIconItem();}
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = this.leftPos + 10; int j = this.topPos + 30;
        if (button == 0 && mouseX >= (double)i && mouseX < (double)(i+14) && mouseY >= (double)j && mouseY < (double)(j + 14)) {
            this.displayOptions = !this.displayOptions;
        } else if (this.displayOptions) {
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
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override public void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}
    @Override
    public void renderBg(GuiGraphics gui, float p_283080_, int p_281303_, int p_283275_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        gui.blit(RenderType::guiTextured,this.containerColor, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        if (this.displayOptions) {
            gui.renderFakeItem(Iconstack,this.leftPos+9,this.topPos+13);
            gui.renderItemDecorations(this.font,Iconstack,this.leftPos+10,this.topPos+13);
        }
        if (this.displayName) {
            /*int ctab = this.iPlayer.getContainerTab(); String cname = this.iPlayer.getContainerName();
            if (cname != this.containerName || ctab != this.receivedID) { this.setContainer(); }*/
            boolean hasprefix = this.containerName.startsWith("!");
            String sname = hasprefix?this.containerName.split("!")[1]:this.containerName;
            int fw = font.width(sname); int total = 5+fw+7; boolean toobig = total>= 203;
            if (toobig && total < 217) { fw = 205; total = 217; }
            gui.blit(RenderType::guiTextured,this.containerColor,i,j-13,223.0F,39.0F,5,18,256,256);
            gui.blit(RenderType::guiTextured,this.containerColor,i+5,j-13,228.0F,39.0F,fw+7,18,17,18,256,256);
            if (toobig) {
                gui.blit(RenderType::guiTextured,this.containerColor,i+total,j-13,245.0F,58.0F,5,23,256,256);
                gui.blit(RenderType::guiTextured,this.containerColor,i+210,j+5,223.0F,76.0F,5,5,256,256);
                gui.blit(RenderType::guiTextured,this.containerColor,i+215,j+5,228.0F,76.0F,total-215,5,5,5,256,256);
            } else {
                gui.blit(RenderType::guiTextured,this.containerColor,i+total,j-13, 245.0F, 39.0F, 5, 18, 256, 256);
            }
            gui.drawString(this.font,sname,i+8, j-6, 4210752, false);
        }
    }

    @Override
    public void renderItemCountBg(GuiGraphics guiGraphics, int x, int y, ItemStack stack, String countString, boolean plrInv, String title) { int nm = stack.getCount();
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
        if (nm < 10) {
            guiGraphics.blit(RenderType::guiTextured,
                    plrInv?INVENTORY_LOCATION:this.containerColor,x+10,yf,245.0F,4.0F,8,8,256,256);
        } else {
            guiGraphics.blit(RenderType::guiTextured,
                    plrInv?INVENTORY_LOCATION:this.containerColor,x+6,yf,240.0F,16.0F,13,8,256,256);
        }
        guiGraphics.drawString(font,s, x + (nm>49&&nm<60?20:20) - 2 - font.width(s), y + 8, txtcolor, !plrInv);
        guiGraphics.pose().popPose();
    }
}
