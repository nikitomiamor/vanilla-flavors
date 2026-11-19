package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.BetaFog;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.AtmosphericFogEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Marks that this frame uses the normal air fog (not water, lava, blindness...). */
@Mixin(AtmosphericFogEnvironment.class)
public abstract class AtmosphericFogEnvironmentMixin {
    @Inject(method = "setupFog", at = @At("TAIL"))
    private void vanillaFlavors$markAtmospheric(FogData fog, Camera camera, ClientLevel level, float renderDistance,
                                              DeltaTracker deltaTracker, CallbackInfo ci) {
        BetaFog.atmospheric = true;
    }
}
