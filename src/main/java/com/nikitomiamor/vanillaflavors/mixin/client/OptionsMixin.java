package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.ModKeys;
import java.util.Arrays;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Anade la tecla del lightmap a Controles (sin Fabric API), antes de leer options.txt para que se guarde. */
@Mixin(Options.class)
public abstract class OptionsMixin {
    @Shadow @Final @Mutable public KeyMapping[] keyMappings;

    @Inject(method = "load", at = @At("HEAD"))
    private void vanillaFlavors$addKeys(CallbackInfo ci) {
        if (Arrays.asList(keyMappings).contains(ModKeys.ALL[0])) return;
        KeyMapping[] keys = Arrays.copyOf(keyMappings, keyMappings.length + ModKeys.ALL.length);
        System.arraycopy(ModKeys.ALL, 0, keys, keyMappings.length, ModKeys.ALL.length);
        keyMappings = keys;
    }
}
