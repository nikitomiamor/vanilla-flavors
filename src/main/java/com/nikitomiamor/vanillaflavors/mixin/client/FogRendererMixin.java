package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.BetaFog;
import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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
        BetaFog.ceiling = level.dimensionType().hasCeiling();
    }

    /**
     * Runs on the last write of setupFog, so Sodium (which copies the values when the method returns) always sees it.
     * Modern clear-weather air also has an environmental haze that reaches far past the render distance;
     * b1.7.3 had none, so it is dropped. Rain and other short environmental fog stay.
     */
    @Redirect(method = "setupFog", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
        target = "Lnet/minecraft/client/renderer/fog/FogData;renderDistanceEnd:F"))
    private void vanillaFlavors$betaFog(FogData fog, float end) {
        fog.renderDistanceEnd = end;
        if (!BetaFog.atmospheric) return;
        VanillaFlavorsConfig config = VanillaFlavorsConfig.get();
        if (!BetaFog.ceiling) {
            // no fade toward the horizon: one flat color, like the sky before Infdev 20100618
            if (config.flatSkyModel) fog.skyEnd = Float.MAX_VALUE;
        }
        if (!config.betaFog) return;
        fog.renderDistanceStart = BetaFog.ceiling ? 0.0F : end * 0.25F;
        if (fog.environmentalEnd > end) {
            fog.environmentalStart = Float.MAX_VALUE;
            fog.environmentalEnd = Float.MAX_VALUE;
        }
    }
}
