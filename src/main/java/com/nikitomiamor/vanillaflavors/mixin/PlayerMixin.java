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
     * View and hand tilt when falling or jumping (1.12.2 EntityPlayer.cameraPitch).
     * Target atan(-velY * 0.2) * 15 degrees, 0 on the ground or when dead; moves 80% closer each tick.
     * GameRendererMixin applies it in bobView, so it only shows with view bobbing enabled.
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
     * The head can turn up to 75 degrees from the torso, as before 1.12 (1.11 updateDistance).
     * Current vanilla uses 50; the 15 limit while blocking with a shield is kept.
     * Together with LivingEntityMixin, walking backwards leaves the torso turned 75 degrees sideways.
     */
    @Inject(method = "getMaxHeadRotationRelativeToBody", at = @At("HEAD"), cancellable = true)
    private void vanillaFlavors$classicHeadTurn(CallbackInfoReturnable<Float> cir) {
        if (VanillaFlavorsConfig.get().classicHeadTurn && !((Player) (Object) this).isBlocking()) cir.setReturnValue(75.0F);
    }

    /** If a player named Notch dies, they drop an apple (1.2.5 EntityPlayer.onDeath). */
    @Inject(method = "dropEquipment", at = @At("HEAD"))
    private void vanillaFlavors$notchApple(ServerLevel level, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (VanillaFlavorsConfig.get().notchApple && "Notch".equals(self.getName().getString())) {
            ItemEntity apple = self.createItemStackToDrop(new ItemStack(Items.APPLE), true, false);
            if (apple != null) level.addFreshEntity(apple);
        }
    }
}
