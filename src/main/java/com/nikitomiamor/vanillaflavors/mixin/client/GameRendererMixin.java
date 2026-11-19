package com.nikitomiamor.vanillaflavors.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import com.nikitomiamor.vanillaflavors.FallTiltAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * When falling, the view and the first-person hand tilt with the speed (the other way when jumping),
 * as in 1.12.2 EntityRenderer.setupViewBobbing. bobView is used for both the camera and the hand.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "bobView", at = @At("TAIL"))
    private void vanillaFlavors$fallTilt(CameraRenderState camera, PoseStack poseStack, CallbackInfo ci) {
        if (!VanillaFlavorsConfig.get().fallTilt) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.getCameraEntity() instanceof FallTiltAccess tilt) {
            float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
            poseStack.rotateDegrees(Axis.XP, tilt.vanillaFlavors$fallTilt(partialTick));
        }
    }
}
