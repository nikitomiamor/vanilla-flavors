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

/** Dano sin origen (caida, fuego, hambre...): la camara se inclina a un lado al azar, como en 1.2.5. */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    /**
     * Caminar hacia atras gira el cuerpo de lado, como antes de 1.12 (17w13a).
     * Desde 17w13a, tick() voltea 180 el objetivo del cuerpo cuando el movimiento esta entre 95 y 265 grados
     * de la mirada; subir ese 95 hace que la condicion nunca se cumpla y el cuerpo apunte hacia donde se mueve.
     */
    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 95.0F))
    private float vanillaFlavors$sidewaysBackwardWalk(float original) {
        return (Object) this instanceof Player && VanillaFlavorsConfig.get().sidewaysBackwardWalk ? Float.MAX_VALUE : original;
    }

    /**
     * Ultimo paso de EntityLivingBase.updateDistance (1.2.5 a 1.11): tras limitar la cabeza a 75 grados del torso,
     * si la diferencia pasa de 50 el torso se acerca un 20% mas a la mirada. Al caminar hacia atras queda a ~60 grados.
     * El tickHeadTurn de ahora solo limita, sin este empujon.
     */
    @Inject(method = "tickHeadTurn", at = @At("TAIL"))
    private void vanillaFlavors$oldBodyNudge(float yBodyRotT, CallbackInfo ci) {
        if (!((Object) this instanceof Player player) || !VanillaFlavorsConfig.get().classicHeadTurn) return;
        float diff = Mth.wrapDegrees(player.getYRot() - player.yBodyRot);
        if (diff * diff > 2500.0F) player.yBodyRot += diff * 0.2F;
    }

    @Shadow @Final private LivingEntity.SwingState swingState;

    /**
     * Golpe de b1.7.3 (EntityPlayer.swingItem): cada clic reinicia la animacion, no solo pasada la mitad.
     * Solo en clics (ClickSwing): al minar el juego llama a swing() cada tick, y reiniciar ahi dejaba el brazo
     * quieto. Minando, el reinicio normal (pasada la mitad de 8 ticks = cada 5) ya coincide con b1.7.3,
     * que repetia el clic cada 5 ticks con el boton pulsado.
     */
    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/component/SwingAnimation;Z)Z", at = @At("HEAD"))
    private void vanillaFlavors$alwaysRestartSwing(InteractionHand hand, SwingAnimation animation, boolean sendToSelf,
                                                 CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player && player.level().isClientSide() && ClickSwing.active
            && VanillaFlavorsConfig.get().oldArmSwing) {
            ((SwingStateAccessor) this.swingState).vanillaFlavors$setCurrentSwing(null);
        }
    }

    /** El golpe duraba 8 ticks en b1.7.3 y 1.2.5; ahora 6. Solo cambia la duracion por defecto (no lanzas y similares). */
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
