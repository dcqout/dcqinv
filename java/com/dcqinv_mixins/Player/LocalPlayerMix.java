package com.dcqinv_mixins.Player;

import com.mojang.authlib.GameProfile;
import com.dcqinv.Content.world.Mobs.IPlayerGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMix extends AbstractClientPlayer implements IPlayerGui {
    public LocalPlayerMix(ClientLevel clientLevel, GameProfile gameProfile) {super(clientLevel, gameProfile);}

    @Override
    public Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }
}
