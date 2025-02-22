package com.dcqinv_mixins.Player.Menus;

import com.dcqinv.Content.PlayerGui.CustomSlots.CustomCreativeSlot;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(CreativeModeInventoryScreen.ItemPickerMenu.class)
public abstract class ItemPickerMix extends AbstractContainerMenu {
    protected ItemPickerMix(@Nullable MenuType<?> menuType, int containerId) {super(menuType, containerId);}
    protected int a = 0; protected int b = 0;

    @ModifyArg(method = "<init>(Lnet/minecraft/world/entity/player/Player;)V",at = @At(value = "INVOKE", target =
            "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot PickerMenu(Slot arg) {
    Slot nSlot = new CustomCreativeSlot(arg.container,arg.getContainerSlot(),(9+(b*2))+b*18,(18+a)+a*18);
    b += 1; if (b == 9) { a += 1; b = 0; }
    return nSlot;}

    @ModifyArgs(method = "<init>(Lnet/minecraft/world/entity/player/Player;)V",at = @At(value = "INVOKE", target =
            "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;addInventoryHotbarSlots(Lnet/minecraft/world/Container;II)V"))
    protected void PickerStandardSlots(Args args) { a=0; b=0;args.set(1,9); args.set(2,((Integer)args.get(2))+4);}

    //this.addSlot(new CreativeModeInventoryScreen.CustomCreativeSlot(CreativeModeInventoryScreen.CONTAINER, i * 9 + j, 9 +j * 18, 18 +i * 18));


}
