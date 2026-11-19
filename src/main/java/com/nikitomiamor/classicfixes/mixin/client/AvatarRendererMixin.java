package com.nikitomiamor.classicfixes.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nikitomiamor.classicfixes.ClassicFixesConfig;
import com.nikitomiamor.classicfixes.RidingArmState;
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
 * "Brazo torcido" en primera persona (MC-1349 / MC-26638). Si el modelo del jugador se dibujo montado por ultima vez
 * (en F5, en el inventario o al ver a otro jugador montado), la mano vacia en primera persona sale con los -36 grados
 * de la pose de montar. Se arregla solo al volver a dibujar el modelo sin montar (F5 o inventario fuera del vehiculo).
 */
@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {
    @Inject(method = "renderHand", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/UvMapping;)V"))
    private void classicFixes$twistedArm(PoseStack poseStack, SubmitNodeCollector collector, int light, Identifier skin,
                                         ModelPart arm, boolean sleeve, CallbackInfo ci) {
        if (!ClassicFixesConfig.get().twistedArm) return;
        Object model = ((LivingEntityRenderer<?, ?, ?>) (Object) this).getModel();
        if (model instanceof RidingArmState riding && riding.classicFixes$lastPoseWasRiding()) {
            arm.xRot += -0.62831855F;
        }
    }
}
