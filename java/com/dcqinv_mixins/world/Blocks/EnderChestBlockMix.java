package com.dcqinv_mixins.world.Blocks;

import com.dcqinv.Content.world.Mobs.IPlayerGui;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(EnderChestBlock.class)
public class EnderChestBlockMix {

    private AABB getBpos(BlockPos bp) {
        return new AABB((double)bp.getX()-0.1,(double)bp.getY()-0.1,(double)bp.getZ()-0.1,(double)bp.getX()+1.1,(double)bp.getY()+1.1,(double)bp.getZ()+1.1);
    }

    @Inject(method = "useWithoutItem", at = @At("HEAD"))
    protected void useWithoutItem(BlockState bs, Level lvl, BlockPos bp, Player plr, BlockHitResult hr, CallbackInfoReturnable<InteractionResult> info) {
        IPlayerGui player = (IPlayerGui) plr;
        List<ItemFrame> frameList = lvl.getEntitiesOfClass(ItemFrame.class,getBpos(bp));
        if (!BuiltInRegistries.CREATIVE_MODE_TAB.get(0).get().value().hasAnyItems() && plr instanceof LocalPlayer localPlr) {
            new CreativeModeInventoryScreen(localPlr,localPlr.connection.enabledFeatures(),player.getMinecraft().options.operatorItemsTab().get());
        }
        if (frameList.isEmpty()) { player.setContainerName("");player.setContainerTab(-1);return;}
        ItemStack itemStack = frameList.getFirst().getItem();
        if (itemStack.isEmpty()) { player.setContainerName("");player.setContainerTab(-1);return;}
        String custom = itemStack.getCustomName()!=null?itemStack.getCustomName().getString():"";
        player.setContainerName(custom);
        if (custom.startsWith("!")) { player.setContainerTab(-1); return; }
        for (int fall = 0; fall < BuiltInRegistries.CREATIVE_MODE_TAB.size();fall++) {
            Optional<Holder.Reference<CreativeModeTab>> tabw = BuiltInRegistries.CREATIVE_MODE_TAB.get(fall);
            if (tabw.isEmpty()) {player.setContainerTab(-1); return;} else
            if (!(tabw.get().is(CreativeModeTabs.HOTBAR) || tabw.get().is(CreativeModeTabs.SEARCH) || tabw.get().is(CreativeModeTabs.OP_BLOCKS)
                    || tabw.get().is(CreativeModeTabs.INVENTORY))) {
                if (tabw.get().value().contains(itemStack.getItem().getDefaultInstance())) {
                    player.setContainerTab(fall); return;
                }
            }
        }
    }

}
