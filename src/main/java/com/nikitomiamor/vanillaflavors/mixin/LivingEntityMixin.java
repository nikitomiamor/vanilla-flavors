package com.nikitomiamor.vanillaflavors.mixin;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import com.nikitomiamor.vanillaflavors.ClickSwing;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.SwingAnimation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Damage with no source (fall, fire, hunger...): the camera tilts to a random side, as in 1.2.5. */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    /**
     * Walking backwards turns the body sideways, as before 1.12 (17w13a).
     * Since 17w13a, tick() flips the body target 180 degrees when movement is between 95 and 265 degrees
     * from the look direction; raising that 95 makes the condition never true and the body points where it moves.
     */
    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 95.0F))
    private float vanillaFlavors$sidewaysBackwardWalk(float original) {
        return (Object) this instanceof Player && VanillaFlavorsConfig.get().sidewaysBackwardWalk ? Float.MAX_VALUE : original;
    }

    /**
     * Last step of EntityLivingBase.updateDistance (1.2.5 to 1.11): after clamping the head to 75 degrees from the torso,
     * if the difference is over 50 the torso moves 20% closer to the look direction. Walking backwards it ends up at ~60 degrees.
     * The current tickHeadTurn only clamps, without this push.
     */
    @Inject(method = "tickHeadTurn", at = @At("TAIL"))
    private void vanillaFlavors$oldBodyNudge(float yBodyRotT, CallbackInfo ci) {
        if (!((Object) this instanceof Player player) || !VanillaFlavorsConfig.get().classicHeadTurn) return;
        float diff = Mth.wrapDegrees(player.getYRot() - player.yBodyRot);
        if (diff * diff > 2500.0F) player.yBodyRot += diff * 0.2F;
    }

    @Shadow @Final private LivingEntity.SwingState swingState;

    /**
     * b1.7.3 swing (EntityPlayer.swingItem): every click restarts the animation, not only past the halfway point.
     * Only on clicks (ClickSwing): while mining the game calls swing() every tick, and restarting there froze
     * the arm. While mining, the normal restart (past half of 8 ticks = every 5) already matches b1.7.3,
     * which repeated the click every 5 ticks while the button was held.
     */
    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/component/SwingAnimation;Z)Z", at = @At("HEAD"))
    private void vanillaFlavors$alwaysRestartSwing(InteractionHand hand, SwingAnimation animation, boolean sendToSelf,
                                                 CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player && player.level().isClientSide() && ClickSwing.active
            && VanillaFlavorsConfig.get().oldArmSwing) {
            ((SwingStateAccessor) this.swingState).vanillaFlavors$setCurrentSwing(null);
        }
    }

    /** The swing lasted 8 ticks in b1.7.3 and 1.2.5; now 6. Only changes the default duration (not spears and the like). */
    @Redirect(method = "getModifiedSwingDuration",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/SwingAnimation;duration()I"))
    private int vanillaFlavors$slowerSwing(SwingAnimation animation) {
        int duration = animation.duration();
        return (Object) this instanceof Player && VanillaFlavorsConfig.get().oldArmSwing
            && duration == SwingAnimation.DEFAULT.duration() ? 8 : duration;
    }

    @Inject(method = "handleDamageEvent", at = @At("TAIL"))
    private void vanillaFlavors$randomTilt(DamageSource source, CallbackInfo ci) {
        if (VanillaFlavorsConfig.get().randomDamageTilt
            && (Object) this instanceof Player player && player.level().isClientSide()
            && source.getEntity() == null && source.getDirectEntity() == null && source.getSourcePosition() == null) {
            ((PlayerAccessor) player).vanillaFlavors$setHurtDir((int) (Math.random() * 2.0) * 180.0F);
        }
    }
}
