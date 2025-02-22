package com.dcqinv_mixins.Player;

import com.dcqinv.Content.world.Mobs.IPlayerGui;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerMix extends LivingEntity implements IPlayerGui {
    private String ContainerName = "";
    private int ContainerID = -1;
    @Nullable
    private DyeColor shulkerc;

    @Override
    public int getContainerTab() {
         return this.ContainerID;
    }
    @Override
    public void setContainerTab(int containerTabID) {
        this.ContainerID = containerTabID;
    }
    @Override
    public void setContainerName(String NCname) {
        this.ContainerName = NCname;
    }
    @Override
    public void setShulker(@Nullable DyeColor color) {
        this.shulkerc = color;
    }
    @Nullable
    @Override
    public DyeColor getShulker() {
        return this.shulkerc;
    }
    @Override
    public String getContainerName() {
        return this.ContainerName;
    }

    protected PlayerMix(EntityType<? extends LivingEntity> plr, Level lvl) {
        super(plr, lvl);
    }
}
