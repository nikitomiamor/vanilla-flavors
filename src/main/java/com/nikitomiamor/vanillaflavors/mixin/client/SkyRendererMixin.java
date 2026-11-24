package com.nikitomiamor.vanillaflavors.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.nikitomiamor.vanillaflavors.ClassicSky;
import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.world.level.MoonPhase;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Sky history options. The lower half uses the formula from 1.2.5 (RenderGlobal.renderSky): deep blue
 * (r * 0.2 + 0.04, g * 0.2 + 0.04, b * 0.6 + 0.1) at horizon height, drawn after the sun, moon and stars so it hides them.
 * It reuses the vanilla dark disc draw call with another color and height.
 */
@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {
    @Shadow
    private void renderDarkDisc(RenderPass renderPass) {}

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void vanillaFlavors$classicSky(ClientLevel level, float partialTick, Camera camera, SkyRenderState state, CallbackInfo ci) {
        VanillaFlavorsConfig config = VanillaFlavorsConfig.get();
        state.skyColor = config.skyColorStyle.apply(state.skyColor);
        if (!config.sunriseGlow) state.sunriseAndSunsetColor = new Vector4f(0.0F);

        double aboveHorizon = camera.position().y - level.getLevelData().getHorizonHeight(level);
        ClassicSky.drawLowerSky = aboveHorizon >= 0.0 && (config.deepBlueLowerSky || config.lowerSkyHidesCelestials);
        ClassicSky.lowerSkyOffset = (float) -(aboveHorizon - 16.0);
        Vector3fc sky = state.skyColor;
        if (config.deepBlueLowerSky) {
            ClassicSky.LOWER_SKY_COLOR.set(sky.x() * 0.2F + 0.04F, sky.y() * 0.2F + 0.04F, sky.z() * 0.6F + 0.1F, 1.0F);
        } else {
            ClassicSky.LOWER_SKY_COLOR.set(sky.x(), sky.y(), sky.z(), 1.0F);
        }
    }

    /** There was no sun or moon before Indev 20100211-2327. */
    @Inject(method = "renderSun", at = @At("HEAD"), cancellable = true)
    private void vanillaFlavors$noSun(RenderPass renderPass, float rainBrightness, PoseStack poseStack, CallbackInfo ci) {
        if (vanillaFlavors$hideCelestials()) ci.cancel();
    }

    @Inject(method = "renderMoon", at = @At("HEAD"), cancellable = true)
    private void vanillaFlavors$noMoon(RenderPass renderPass, MoonPhase moonPhase, float rainBrightness, PoseStack poseStack, CallbackInfo ci) {
        if (vanillaFlavors$hideCelestials()) ci.cancel();
    }

    @Unique
    private static boolean vanillaFlavors$hideCelestials() {
        return !VanillaFlavorsConfig.get().sunAndMoon;
    }

    @Inject(method = "renderSunMoonAndStars", at = @At("TAIL"))
    private void vanillaFlavors$lowerSky(RenderPass renderPass, PoseStack poseStack, float sunAngle, float moonAngle, float starAngle,
                                         MoonPhase moonPhase, float rainBrightness, float starBrightness, CallbackInfo ci) {
        if (!ClassicSky.drawLowerSky) return;
        ClassicSky.drawingLowerSky = true;
        renderDarkDisc(renderPass);
        ClassicSky.drawingLowerSky = false;
    }

    @ModifyArg(method = "renderDarkDisc", index = 1, at = @At(value = "INVOKE",
        target = "Lorg/joml/Matrix4fStack;translate(FFF)Lorg/joml/Matrix4f;"))
    private float vanillaFlavors$lowerSkyHeight(float y) {
        return ClassicSky.drawingLowerSky ? ClassicSky.lowerSkyOffset : y;
    }

    @ModifyArg(method = "renderDarkDisc", index = 1, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/DynamicGpuData;writeTransform(Lorg/joml/Matrix4f;Lorg/joml/Vector4f;)Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;"))
    private Vector4f vanillaFlavors$lowerSkyColor(Vector4f color) {
        return ClassicSky.drawingLowerSky ? new Vector4f(ClassicSky.LOWER_SKY_COLOR) : color;
    }
}
