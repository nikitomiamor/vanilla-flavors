package com.nikitomiamor.classicfixes.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.nikitomiamor.classicfixes.ClassicFixesConfig;
import com.nikitomiamor.classicfixes.FallTiltAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Al caer, la vista y la mano en primera persona se inclinan segun la velocidad (al saltar, al reves),
 * como en EntityRenderer.setupViewBobbing de 1.12.2. bobView se usa para la camara y para la mano.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "bobView", at = @At("TAIL"))
    private void classicFixes$fallTilt(CameraRenderState camera, PoseStack poseStack, CallbackInfo ci) {
        if (!ClassicFixesConfig.get().fallTilt) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.getCameraEntity() instanceof FallTiltAccess tilt) {
            float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
            poseStack.rotateDegrees(Axis.XP, tilt.classicFixes$fallTilt(partialTick));
        }
    }
}
