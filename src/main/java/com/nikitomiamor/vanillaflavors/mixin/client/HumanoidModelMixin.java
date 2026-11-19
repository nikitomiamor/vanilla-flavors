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
 * Bug MC-1349 (1.4.2 to 1.8.8): the model remembers whether the last entity it drew was riding.
 * In those versions ModelBiped.isRiding was never cleared, so the first-person hand inherited
 * the "arms forward" pose from when the model was drawn riding (F5, inventory or another player).
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
