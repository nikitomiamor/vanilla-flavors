package com.nikitomiamor.vanillaflavors.mixin;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import com.nikitomiamor.vanillaflavors.FallTiltAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin implements FallTiltAccess {
    /**
     * Inclinacion de la vista y la mano al caer o saltar (EntityPlayer.cameraPitch de 1.12.2).
     * Objetivo atan(-velY * 0.2) * 15 grados, 0 en el suelo o muerto; se acerca un 80% cada tick.
     * GameRendererMixin la aplica en bobView, asi que solo se nota con el balanceo al caminar activado.
     */
    @Unique private float vanillaFlavors$tilt;
    @Unique private float vanillaFlavors$tiltO;

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void vanillaFlavors$storeOldTilt(CallbackInfo ci) {
        this.vanillaFlavors$tiltO = this.vanillaFlavors$tilt;
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void vanillaFlavors$updateTilt(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        float target = (float) (Math.atan(-self.getDeltaMovement().y * 0.2F) * 15.0);
        if (self.onGround() || self.getHealth() <= 0.0F) target = 0.0F;
        this.vanillaFlavors$tilt += (target - this.vanillaFlavors$tilt) * 0.8F;
    }

    @Override
    public float vanillaFlavors$fallTilt(float partialTick) {
        return this.vanillaFlavors$tiltO + (this.vanillaFlavors$tilt - this.vanillaFlavors$tiltO) * partialTick;
    }

    /**
     * La cabeza puede girar hasta 75 grados respecto al torso, como antes de 1.12 (updateDistance de 1.11).
     * El vanilla actual usa 50; se respeta el limite de 15 al bloquear con escudo.
     * Junto con LivingEntityMixin, al caminar hacia atras el torso queda girado 75 grados de lado.
     */
    @Inject(method = "getMaxHeadRotationRelativeToBody", at = @At("HEAD"), cancellable = true)
    private void vanillaFlavors$classicHeadTurn(CallbackInfoReturnable<Float> cir) {
        if (VanillaFlavorsConfig.get().classicHeadTurn && !((Player) (Object) this).isBlocking()) cir.setReturnValue(75.0F);
    }

    /** Si muere un jugador llamado Notch, suelta una manzana (EntityPlayer.onDeath de 1.2.5). */
    @Inject(method = "dropEquipment", at = @At("HEAD"))
    private void vanillaFlavors$notchApple(ServerLevel level, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (VanillaFlavorsConfig.get().notchApple && "Notch".equals(self.getName().getString())) {
            ItemEntity apple = self.createItemStackToDrop(new ItemStack(Items.APPLE), true, false);
            if (apple != null) level.addFreshEntity(apple);
        }
    }
}
