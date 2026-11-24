package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** The sky had no clouds before Classic 0.0.14a. Leaves the video setting itself untouched. */
@Mixin(Options.class)
public abstract class OptionsCloudsMixin {
    @Inject(method = "getCloudStatus", at = @At("HEAD"), cancellable = true)
    private void vanillaFlavors$noClouds(CallbackInfoReturnable<CloudStatus> cir) {
        if (!VanillaFlavorsConfig.get().skyClouds) cir.setReturnValue(CloudStatus.OFF);
    }
}
