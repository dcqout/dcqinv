package com.dcqinv_mixins.Player.Menus;

import com.dcqinv.Content.PlayerGui.CustomSlots;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseInventoryMenu.class)
public abstract class HorseInventoryMenuMix extends AbstractContainerMenu {
    protected HorseInventoryMenuMix(@Nullable MenuType<?> menuType, int containerId) {super(menuType, containerId);}
    protected int a; protected int columns; protected AbstractHorse Dhorse; protected Container DhorseContainer; protected Container DarmorContainer;
    private static final ResourceLocation SADDLE_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot/saddle");
    private static final ResourceLocation LLAMA_ARMOR_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot/llama_armor");
    private static final ResourceLocation ARMOR_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot/horse_armor");

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/animal/horse/AbstractHorse;I)V"
            ,at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;startOpen(Lnet/minecraft/world/entity/player/Player;)V") )
    public void HorseInventoryMenuGet(int containerId, Inventory inventory, Container horseContainer, final AbstractHorse horse, int columns, CallbackInfo info) {
        this.columns = columns; a = 100; this.DhorseContainer = horseContainer; this.DarmorContainer = horse.getBodyArmorAccess(); this.Dhorse = horse;
    }

    @Redirect(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/animal/horse/AbstractHorse;I)V"
            ,at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/inventory/HorseInventoryMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    public Slot AddAllSlots(HorseInventoryMenu horseInventoryMenu, Slot arg) {
        if (a == 100) { a += 1;
            return this.addSlot(new Slot(DhorseContainer, 0, 10, 14) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.is(Items.SADDLE) && !this.hasItem() && Dhorse.isSaddleable();
                }
                @Override
                public boolean isActive() {
                    return Dhorse.isSaddleable();
                }
                @Override
                public ResourceLocation getNoItemIcon() {
                    return SADDLE_SLOT_SPRITE;
                }
            });
        } else if (a == 101) { a = 0;
            ResourceLocation resourcelocation = Dhorse instanceof Llama ? LLAMA_ARMOR_SLOT_SPRITE : ARMOR_SLOT_SPRITE;
            return this.addSlot(new CustomSlots().new ArmorSlot(DarmorContainer, Dhorse, EquipmentSlot.BODY, 0, 10, 34, resourcelocation) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return Dhorse.isEquippableInSlot(stack, EquipmentSlot.BODY);
                }
                @Override
                public boolean isActive() {
                    return Dhorse.canUseSlot(EquipmentSlot.BODY);
                }
            });
        }
        if (columns > 0 && a != columns) { a = columns;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < columns; j++) {
                    this.addSlot(new Slot(DhorseContainer, 1 + j + i * columns, (110 + (j * 2)) + j * 18, (14 + i) + i * 18));
                }
            }
        }
    return new Slot(DhorseContainer,500,0,0);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            int i = this.DhorseContainer.getContainerSize() + 1;
            if (index < i) {
                if (!this.moveItemStackTo(itemstack1, i, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).mayPlace(itemstack1) && !this.getSlot(1).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).mayPlace(itemstack1)) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i <= 1 || !this.moveItemStackTo(itemstack1, 2, i, false)) {
                int j = i + 41;
                int k = j + 9;
                if (index >= j && index < k) {
                    if (!this.moveItemStackTo(itemstack1, i, j, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= i && index < j) {
                    if (!this.moveItemStackTo(itemstack1, j, k, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, j, j, false)) {
                    return ItemStack.EMPTY;
                }

                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }
}
