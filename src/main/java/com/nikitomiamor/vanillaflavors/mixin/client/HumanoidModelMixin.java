package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.RidingArmState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bug MC-1349 (1.4.2 a 1.8.8): el modelo recuerda si la ultima entidad que dibujo iba montada.
 * En esas versiones ModelBiped.isRiding nunca se limpiaba, asi que la mano en primera persona heredaba
 * la pose de "brazos hacia adelante" de cuando el modelo se dibujo montado (F5, inventario u otro jugador).
 */
@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin implements RidingArmState {
    @Unique
    private boolean vanillaFlavors$wasRiding;

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("HEAD"))
    private void vanillaFlavors$rememberRiding(HumanoidRenderState state, CallbackInfo ci) {
        this.vanillaFlavors$wasRiding = state.isPassenger;
    }

    @Override
    public boolean vanillaFlavors$lastPoseWasRiding() {
        return this.vanillaFlavors$wasRiding;
    }
}
