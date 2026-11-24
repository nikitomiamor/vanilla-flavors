package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.BetaFog;
import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.AtmosphericFogEnvironment;
import org.joml.Vector4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Marks that this frame uses the normal air fog (not water, lava, blindness...). */
@Mixin(AtmosphericFogEnvironment.class)
public abstract class AtmosphericFogEnvironmentMixin {
    @Inject(method = "setupFog", at = @At("TAIL"))
    private void vanillaFlavors$markAtmospheric(FogData fog, Camera camera, ClientLevel level, float renderDistance,
                                              DeltaTracker deltaTracker, CallbackInfo ci) {
        BetaFog.atmospheric = true;
    }

    /** The fog also picks up the sunrise color when looking at the sun, so the glow option has to clear it here too. */
    @Redirect(method = "getBaseColor", at = @At(value = "INVOKE", target = "Lorg/joml/Vector4fc;w()F"))
    private float vanillaFlavors$noSunsetFog(Vector4fc sunriseColor) {
        return VanillaFlavorsConfig.get().sunriseGlow ? sunriseColor.w() : 0.0F;
    }
}
