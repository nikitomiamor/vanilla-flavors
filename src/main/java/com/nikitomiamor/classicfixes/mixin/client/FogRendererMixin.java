package com.nikitomiamor.classicfixes.mixin.client;

import com.nikitomiamor.classicfixes.BetaFog;
import com.nikitomiamor.classicfixes.ClassicFixesConfig;
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
 * Niebla de distancia de b1.7.3 (EntityRenderer.setupFog, rama lineal):
 * empieza a 1/4 de la distancia de renderizado y se cierra en el borde; en el Nether empieza en 0.
 * La moderna solo difumina los ultimos bloques (borde - clamp(borde/10, 4, 64)).
 * Solo toca la niebla del aire: agua, lava, nieve polvo, ceguera y oscuridad quedan como estan.
 * Sodium lee estos mismos valores, asi que funciona con el.
 */
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Inject(method = "setupFog", at = @At("HEAD"))
    private void classicFixes$resetFlag(Camera camera, int renderDistance, DeltaTracker deltaTracker, float darkenAmount,
                                        ClientLevel level, CallbackInfoReturnable<FogData> cir) {
        BetaFog.atmospheric = false;
    }

    @Inject(method = "setupFog", at = @At("RETURN"))
    private void classicFixes$betaFog(Camera camera, int renderDistance, DeltaTracker deltaTracker, float darkenAmount,
                                      ClientLevel level, CallbackInfoReturnable<FogData> cir) {
        if (!BetaFog.atmospheric || !ClassicFixesConfig.get().betaFog) return;
        FogData fog = cir.getReturnValue();
        float end = fog.renderDistanceEnd;
        fog.renderDistanceStart = level.dimensionType().hasCeiling() ? 0.0F : end * 0.25F;
    }
}
