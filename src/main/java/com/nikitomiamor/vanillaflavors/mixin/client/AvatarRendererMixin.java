package com.nikitomiamor.vanillaflavors.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import com.nikitomiamor.vanillaflavors.RidingArmState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * First-person "bent arm" (MC-1349 / MC-26638). If the player model was last drawn riding
 * (in F5, in the inventory or when seeing another player riding), the empty first-person hand gets the -36 degrees
 * of the riding pose. It fixes itself once the model is drawn again not riding (F5 or inventory outside the vehicle).
 */
@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {
    @Inject(method = "renderHand", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/UvMapping;)V"))
    private void vanillaFlavors$twistedArm(PoseStack poseStack, SubmitNodeCollector collector, int light, Identifier skin,
                                         ModelPart arm, boolean sleeve, CallbackInfo ci) {
        if (!VanillaFlavorsConfig.get().twistedArm) return;
        Object model = ((LivingEntityRenderer<?, ?, ?>) (Object) this).getModel();
        if (model instanceof RidingArmState riding && riding.vanillaFlavors$lastPoseWasRiding()) {
            arm.xRot += -0.62831855F;
        }
    }
}
