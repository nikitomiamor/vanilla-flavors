package com.nikitomiamor.vanillaflavors;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/** Texto de la pantalla de carga de antes de 12w18a ("Entering the Nether", "Respawning"...). */
public final class LoadingTitle {
    private static @Nullable Component current;

    private LoadingTitle() {}

    public static void set(boolean died, ResourceKey<Level> to, ResourceKey<Level> from) {
        String key = null;
        if (died) key = "respawning";
        else if (to == from) key = null;
        else if (to == Level.NETHER) key = "entering_nether";
        else if (from == Level.NETHER) key = "leaving_nether";
        else if (to == Level.END) key = "entering_end";
        else if (from == Level.END) key = "leaving_end";
        current = key == null ? null : Component.translatable("vanilla_flavors.loading." + key);
    }

    public static @Nullable Component get() {
        return current;
    }

    public static void clear() {
        current = null;
    }
}
