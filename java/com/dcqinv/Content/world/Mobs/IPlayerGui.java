package com.dcqinv.Content.world.Mobs;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

public interface IPlayerGui {
    void setShulker(@Nullable DyeColor color);
    void setContainerName(String name);
    void setContainerTab(int tab);
    String getContainerName();
    int getContainerTab();
    @Nullable DyeColor getShulker();
    Minecraft getMinecraft();
}
