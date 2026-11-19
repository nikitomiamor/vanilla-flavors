package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.BetaFog;
import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * b1.7.3 distance fog (EntityRenderer.setupFog, linear branch):
 * starts at 1/4 of the render distance and closes at the edge; in the Nether it starts at 0.
 * The modern one only blurs the last few blocks (edge - clamp(edge/10, 4, 64)).
 * Only touches the air fog: water, lava, powder snow, blindness and darkness stay as they are.
 * Sodium reads these same values, so it works with it.
 */
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Inject(method = "setupFog", at = @At("HEAD"))
    private void vanillaFlavors$resetFlag(Camera camera, int renderDistance, DeltaTracker deltaTracker, float darkenAmount,
                                        ClientLevel level, CallbackInfoReturnable<FogData> cir) {
        BetaFog.atmospheric = false;
    }

    @Inject(method = "setupFog", at = @At("RETURN"))
    private void vanillaFlavors$betaFog(Camera camera, int renderDistance, DeltaTracker deltaTracker, float darkenAmount,
                                      ClientLevel level, CallbackInfoReturnable<FogData> cir) {
        if (!BetaFog.atmospheric || !VanillaFlavorsConfig.get().betaFog) return;
        FogData fog = cir.getReturnValue();
        float end = fog.renderDistanceEnd;
        fog.renderDistanceStart = level.dimensionType().hasCeiling() ? 0.0F : end * 0.25F;
    }
}
