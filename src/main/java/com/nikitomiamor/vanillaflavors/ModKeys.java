package com.nikitomiamor.vanillaflavors;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

/** The mod's keys, in their own Controls section. OptionsMixin adds them without Fabric API. */
public final class ModKeys {
    public static final KeyMapping.Category CATEGORY =
        KeyMapping.Category.register(Identifier.fromNamespaceAndPath("vanilla_flavors", "main"));

    /** Opens the config menu (needs Cloth Config). */
    public static final KeyMapping OPEN_CONFIG = new KeyMapping("vanilla_flavors.key.openConfig", GLFW.GLFW_KEY_F6, CATEGORY);
    /** Toggles the lighting style: grayscale / colorized. */
    public static final KeyMapping CYCLE_LIGHTMAP = new KeyMapping("vanilla_flavors.key.cycleLightmap", GLFW.GLFW_KEY_F7, CATEGORY);

    public static final KeyMapping[] ALL = {OPEN_CONFIG, CYCLE_LIGHTMAP};

    private ModKeys() {}
}
