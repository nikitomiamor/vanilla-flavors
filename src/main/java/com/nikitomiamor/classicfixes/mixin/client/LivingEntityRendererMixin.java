package com.nikitomiamor.classicfixes.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nikitomiamor.classicfixes.GhastSwellState;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Inject(method = "scale", at = @At("HEAD"))
    private void classicFixes$ghastSwell(LivingEntityRenderState state, PoseStack poseStack, CallbackInfo ci) {
        if (state instanceof GhastSwellState swell) {
            poseStack.scale(swell.classicFixes$scaleXZ(), swell.classicFixes$scaleY(), swell.classicFixes$scaleXZ());
        }
    }
}
