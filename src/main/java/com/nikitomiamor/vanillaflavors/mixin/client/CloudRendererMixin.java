package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.ClassicSky;
import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import net.minecraft.client.renderer.CloudRenderer;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Infdev clouds are solid bright white. The shader darkens each face (0.7 at the bottom), so the color is raised
 * to cancel that out. It still follows the time of day and weather.
 */
@Mixin(CloudRenderer.class)
public abstract class CloudRendererMixin {
    @ModifyArg(method = "prepare(ILnet/minecraft/client/CloudStatus;FILnet/minecraft/world/phys/Vec3;JF)V", at = @At(value = "INVOKE",
        target = "Lcom/mojang/blaze3d/buffers/Std140Builder;putVec4(Lorg/joml/Vector4fc;)Lcom/mojang/blaze3d/buffers/Std140Builder;"))
    private Vector4fc vanillaFlavors$brightClouds(Vector4fc color) {
        if (VanillaFlavorsConfig.get().skyColorStyle != ClassicSky.ColorStyle.INFDEV) return color;
        float boost = 1.0F / 0.7F;
        return new Vector4f(color.x() * boost, color.y() * boost, color.z() * boost, 1.0F);
    }
}
