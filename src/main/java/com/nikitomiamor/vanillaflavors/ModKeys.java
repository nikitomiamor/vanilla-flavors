package com.nikitomiamor.vanillaflavors;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

/** Teclas del mod, en su propia seccion de Controles. OptionsMixin las anade sin Fabric API. */
public final class ModKeys {
    public static final KeyMapping.Category CATEGORY =
        KeyMapping.Category.register(Identifier.fromNamespaceAndPath("vanilla_flavors", "main"));

    /** Abre el menu de configuracion (necesita Cloth Config). */
    public static final KeyMapping OPEN_CONFIG = new KeyMapping("vanilla_flavors.key.openConfig", GLFW.GLFW_KEY_F6, CATEGORY);
    /** Alterna el estilo de iluminacion: grises / color. */
    public static final KeyMapping CYCLE_LIGHTMAP = new KeyMapping("vanilla_flavors.key.cycleLightmap", GLFW.GLFW_KEY_F7, CATEGORY);

    public static final KeyMapping[] ALL = {OPEN_CONFIG, CYCLE_LIGHTMAP};

    private ModKeys() {}
}
