package com.nikitomiamor.classicfixes.mixin;

import com.nikitomiamor.classicfixes.GhastCharge;
import net.minecraft.world.entity.monster.Ghast;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.entity.monster.Ghast$GhastShootFireballGoal")
public abstract class GhastShootFireballGoalMixin {
    @Shadow @Final private Ghast ghast;
    @Shadow public int chargeTime;

    @Inject(method = "tick", at = @At("TAIL"))
    private void classicFixes$shareCharge(CallbackInfo ci) {
        GhastCharge.publish(this.ghast, this.chargeTime);
    }

    @Inject(method = "stop", at = @At("TAIL"))
    private void classicFixes$clearCharge(CallbackInfo ci) {
        GhastCharge.clear(this.ghast);
    }
}
