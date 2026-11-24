package com.nikitomiamor.vanillaflavors;

import com.nikitomiamor.vanillaflavors.ClassicSky.ColorStyle;
import java.util.Arrays;

/**
 * Sky options as they were in each version of the minecraft.wiki Sky history. Not saved: the current preset
 * is worked out from the options, so changing any of them turns it into CUSTOM.
 * Flags follow the order of {@link #FIELDS}.
 */
public enum SkyPreset {
    CUSTOM(null),
    CAVE_GAME(ColorStyle.EARLY_CLASSIC, false, false, true, false, true, false, false),
    CLASSIC_0_0_14A(ColorStyle.EARLY_CLASSIC, true, false, true, false, true, false, false),
    SURVIVAL_TEST(ColorStyle.CLASSIC, true, false, true, false, true, false, false),
    INDEV_20100211(ColorStyle.CLASSIC, true, true, true, false, true, false, false),
    INFDEV_20100227(ColorStyle.INFDEV, true, true, false, false, true, false, false),
    INFDEV_20100313(ColorStyle.CLASSIC, true, true, true, false, true, false, false),
    INFDEV_20100618(ColorStyle.CLASSIC, true, true, false, false, true, true, true),
    ALPHA_1_2(ColorStyle.BIOME, true, true, false, true, true, true, true),
    V1_10(ColorStyle.BIOME, true, true, false, true, true, false, false),
    V1_21_11(ColorStyle.BIOME, true, true, false, true, false, false, false),
    V26_4(ColorStyle.BIOME, true, true, false, true, false, false, true);

    /** The boolean config fields a preset sets. */
    public static final String[] FIELDS = {"skyClouds", "sunAndMoon", "flatSkyModel", "sunriseGlow",
        "sunsetIgnoresWeather", "deepBlueLowerSky", "lowerSkyHidesCelestials"};

    public final ColorStyle color;
    public final boolean[] flags;

    SkyPreset(ColorStyle color, boolean... flags) {
        this.color = color;
        this.flags = flags;
    }

    public static SkyPreset match(ColorStyle color, boolean[] flags) {
        for (SkyPreset preset : values()) {
            if (preset != CUSTOM && preset.color == color && Arrays.equals(preset.flags, flags)) return preset;
        }
        return CUSTOM;
    }
}
