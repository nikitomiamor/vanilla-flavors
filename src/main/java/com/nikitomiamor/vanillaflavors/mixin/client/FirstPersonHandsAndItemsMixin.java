package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItems.class)
public abstract class FirstPersonHandsAndItemsMixin {
    /** No sway: in b1.7.3 the hand was locked to the camera (the xBob/yBob lag came later). */
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void vanillaFlavors$noArmSway(LocalPlayer player, float partialTick, FirstPersonHandsAndItemsRenderState state, CallbackInfo ci) {
        if (!VanillaFlavorsConfig.get().noArmSway) return;
        state.xBob = state.viewXRot;
        state.yBob = state.viewYRot;
    }

    /** Old swing: the hand does not drop from the 1.9 attack cooldown, only when switching items. */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemSwapScale(F)F"))
    private float vanillaFlavors$noCooldownDip(LocalPlayer player, float partialTick) {
        return VanillaFlavorsConfig.get().oldArmSwing ? 1.0F : player.getItemSwapScale(partialTick);
    }
}
