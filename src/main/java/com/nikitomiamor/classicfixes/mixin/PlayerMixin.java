package com.nikitomiamor.classicfixes.mixin;

import com.nikitomiamor.classicfixes.ClassicFixesConfig;
import com.nikitomiamor.classicfixes.FallTiltAccess;
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
    @Unique private float classicFixes$tilt;
    @Unique private float classicFixes$tiltO;

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void classicFixes$storeOldTilt(CallbackInfo ci) {
        this.classicFixes$tiltO = this.classicFixes$tilt;
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void classicFixes$updateTilt(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        float target = (float) (Math.atan(-self.getDeltaMovement().y * 0.2F) * 15.0);
        if (self.onGround() || self.getHealth() <= 0.0F) target = 0.0F;
        this.classicFixes$tilt += (target - this.classicFixes$tilt) * 0.8F;
    }

    @Override
    public float classicFixes$fallTilt(float partialTick) {
        return this.classicFixes$tiltO + (this.classicFixes$tilt - this.classicFixes$tiltO) * partialTick;
    }

    /**
     * La cabeza puede girar hasta 75 grados respecto al torso, como antes de 1.12 (updateDistance de 1.11).
     * El vanilla actual usa 50; se respeta el limite de 15 al bloquear con escudo.
     * Junto con LivingEntityMixin, al caminar hacia atras el torso queda girado 75 grados de lado.
     */
    @Inject(method = "getMaxHeadRotationRelativeToBody", at = @At("HEAD"), cancellable = true)
    private void classicFixes$classicHeadTurn(CallbackInfoReturnable<Float> cir) {
        if (ClassicFixesConfig.get().classicHeadTurn && !((Player) (Object) this).isBlocking()) cir.setReturnValue(75.0F);
    }

    /** Si muere un jugador llamado Notch, suelta una manzana (EntityPlayer.onDeath de 1.2.5). */
    @Inject(method = "dropEquipment", at = @At("HEAD"))
    private void classicFixes$notchApple(ServerLevel level, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (ClassicFixesConfig.get().notchApple && "Notch".equals(self.getName().getString())) {
            ItemEntity apple = self.createItemStackToDrop(new ItemStack(Items.APPLE), true, false);
            if (apple != null) level.addFreshEntity(apple);
        }
    }
}
