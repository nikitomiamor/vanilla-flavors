package com.nikitomiamor.vanillaflavors;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

/** Values shared between the sky mixins (render thread only). */
public final class ClassicSky {
    /** Daytime sky color: by biome (Alpha 1.2+) or one of the fixed colors from Classic. */
    public enum ColorStyle {
        BIOME(null),
        CLASSIC(new Vector3f(0x99 / 255F, 0xCC / 255F, 1F)),
        EARLY_CLASSIC(new Vector3f(0x7F / 255F, 0xCC / 255F, 1F)),
        /** The completely blue sky of early Infdev (20100227-1414). */
        INFDEV(new Vector3f(0F, 0F, 1F));

        private final Vector3fc color;

        ColorStyle(Vector3fc color) {
            this.color = color;
        }

        /** Keeps the brightness of the current sky (time of day, weather) with the fixed color. */
        public Vector3fc apply(Vector3fc sky) {
            if (color == null) return sky;
            return color.mul(Math.max(sky.x(), Math.max(sky.y(), sky.z())), new Vector3f());
        }
    }

    public static boolean drawLowerSky;
    public static boolean drawingLowerSky;
    public static float lowerSkyOffset;
    public static final Vector4f LOWER_SKY_COLOR = new Vector4f();

    private ClassicSky() {}
}
