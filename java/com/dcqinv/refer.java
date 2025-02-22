package com.dcqinv;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class refer {
    public static final String MODID = "net";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static int[] getColorComponents(int argbColor) {
        int alpha = (argbColor >> 24) & 0xFF;
        int red = (argbColor >> 16) & 0xFF;
        int green = (argbColor >> 8) & 0xFF;
        int blue = argbColor & 0xFF;

        return new int[]{red, green, blue, alpha};
    }

    public static int createARGBColor(int red, int green, int blue, int alpha) {
        alpha = Math.max(0, Math.min(255, alpha));
        red = Math.max(0, Math.min(255, red));
        green = Math.max(0, Math.min(255, green));
        blue = Math.max(0, Math.min(255, blue));
        refer.LOGGER.warn(String.valueOf((alpha << 24) | (red << 16) | (green << 8) | blue));
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
