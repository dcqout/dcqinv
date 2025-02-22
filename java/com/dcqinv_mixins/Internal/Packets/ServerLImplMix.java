package com.dcqinv_mixins.Internal.Packets;

import com.dcqinv.refer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.TickThrottler;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerGamePacketListenerImpl.class) @SuppressWarnings("UnreachableCode")
public class ServerLImplMix {

    @Shadow private final TickThrottler dropSpamThrottler;

    public ServerLImplMix() {
        dropSpamThrottler = null;
    }

    @Overwrite
    public void handleContainerClick(ServerboundContainerClickPacket packet) {
        ServerGamePacketListenerImpl cast = (ServerGamePacketListenerImpl)(Object)this;
        PacketUtils.ensureRunningOnSameThread(packet, cast, cast.player.serverLevel());
        cast.player.resetLastActionTime();
        if (cast.player.containerMenu.containerId == packet.getContainerId()) {
            if (cast.player.isSpectator()) {
                cast.player.containerMenu.sendAllDataToRemote();
            } else if (!cast.player.containerMenu.stillValid(cast.player)) {
                refer.LOGGER.debug("Player {} interacted with invalid menu {}", cast.player, cast.player.containerMenu);
            } else {
                int i = packet.getSlotNum();
                if (!cast.player.containerMenu.isValidSlotIndex(i)) {
                    refer.LOGGER.debug(
                            "Player {} clicked invalid slot index: {}, available slots: {}", cast.player.getName(), i, cast.player.containerMenu.slots.size()
                    );
                } else {
                    boolean flag = packet.getStateId() != cast.player.containerMenu.getStateId();
                    cast.player.containerMenu.suppressRemoteUpdates();
                    cast.player.containerMenu.clicked(i, packet.getButtonNum(), packet.getClickType(), cast.player);

                    for (Int2ObjectMap.Entry<ItemStack> entry : Int2ObjectMaps.fastIterable(packet.getChangedSlots())) {
                        cast.player.containerMenu.setRemoteSlotNoCopy(entry.getIntKey(), entry.getValue());
                    }

                    cast.player.containerMenu.setRemoteCarried(packet.getCarriedItem());
                    cast.player.containerMenu.resumeRemoteUpdates();
                    if (flag) {
                        cast.player.containerMenu.broadcastFullState();
                    } else {
                        cast.player.containerMenu.broadcastChanges();
                    }
                }
            }
        }
    }

    @Overwrite
    public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket packet) {
        ServerGamePacketListenerImpl cast = (ServerGamePacketListenerImpl)(Object)this;
        PacketUtils.ensureRunningOnSameThread(packet, cast, cast.player.serverLevel());
        if (cast.player.gameMode.isCreative()) {
            boolean flag = packet.slotNum() < 0;
            ItemStack itemstack = packet.itemStack();
            if (!itemstack.isItemEnabled(cast.player.level().enabledFeatures())) {
                return;
            }
            boolean flag1 = packet.slotNum() >= 1 && packet.slotNum() <= 58;
            boolean flag2 = itemstack.isEmpty() || itemstack.getCount() <= itemstack.getMaxStackSize();
            if (flag1 && flag2) {
                cast.player.inventoryMenu.getSlot(packet.slotNum()).setByPlayer(itemstack);
                cast.player.inventoryMenu.setRemoteSlot(packet.slotNum(), itemstack);
                cast.player.inventoryMenu.broadcastChanges();
            } else if (flag && flag2) {
                if (dropSpamThrottler.isUnderThreshold()) {
                    dropSpamThrottler.increment();
                    cast.player.drop(itemstack, true);
                } else {
                    refer.LOGGER.warn("[DCQ] Player {} was dropping items too fast in creative mode, ignoring.", cast.player.getName().getString());
                }
            }
        }
    }

}
