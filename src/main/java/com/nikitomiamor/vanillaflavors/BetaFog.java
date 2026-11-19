package com.nikitomiamor.vanillaflavors;

/** State shared between the fog mixins (render thread only). */
public final class BetaFog {
    /** true if the air fog was applied in the current frame. */
    public static boolean atmospheric;

    private BetaFog() {}
}
