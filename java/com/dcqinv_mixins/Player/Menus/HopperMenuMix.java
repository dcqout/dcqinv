package com.dcqinv_mixins.Player.Menus;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(HopperMenu.class)
public abstract class HopperMenuMix extends AbstractContainerMenu {
    protected HopperMenuMix(@Nullable MenuType<?> menuType, int containerId) {super(menuType, containerId);} protected int a = 0;

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;)V",at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/HopperMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot HopperMenu(Slot arg) {Slot nSlot = new Slot(arg.container,arg.getContainerSlot(),(60+(a*2))+a*18,14); a += 1; return nSlot;}

    @ModifyArgs(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;)V",at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/HopperMenu;addStandardInventorySlots(Lnet/minecraft/world/Container;II)V"))
    protected void addStandardInventorySlots(Args args) { args.set(1,8); args.set(2,((Integer)args.get(2))-3);}
}
