package com.dcqinv;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(dcqinv.MODID)
public class dcqinv
{
    public static final String MODID = "dcqinv";
    public dcqinv(IEventBus EventBus, ModContainer modc)
    {
        modc.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

}
