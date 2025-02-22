package com.dcqinv_mixins.Player.Menus;

import com.dcqinv.Content.PlayerGui.IContainerMenu;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ShulkerBoxMenu.class)
public abstract class ShulkerBoxMenuMix extends AbstractContainerMenu implements IContainerMenu {

    private Player player; protected int a = 0; protected int b = 0;

    protected ShulkerBoxMenuMix(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;)V",at = @At("TAIL"))
    public void ShulkerBoxMenu(int containerId, Inventory playerInventory,Container container, CallbackInfo info) {
        this.player = playerInventory.player;
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;)V",at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/ShulkerBoxMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot ShulkerBoxMenu(Slot arg) {ShulkerBoxSlot nSlot = new ShulkerBoxSlot(arg.container,arg.getContainerSlot(),(30+(b*2))+b*18,(9+a)+a*18);
        b += 1; if (b == 9) { a += 1; b = 0; } return nSlot;
    }

    @ModifyArgs(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;)V",at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/ShulkerBoxMenu;addStandardInventorySlots(Lnet/minecraft/world/Container;II)V"))
    protected void addStandardInventorySlots(Args args) { args.set(1,8); args.set(2,((Integer)args.get(2))-6);}



    @Override
    public Player getPlr() {
        return player;
    }
}
