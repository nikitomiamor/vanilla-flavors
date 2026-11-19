package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.LinearInterpolationHandler;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartClientMixin {
    /**
     * El servidor empuja al jugador, pero el movimiento del jugador lo decide el cliente y
     * ese empuje se pierde. Aplicamos aqui los mismos dos empujes que hacia 1.2.5.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void vanillaFlavors$pushLocalPlayer(CallbackInfo ci) {
        AbstractMinecart self = (AbstractMinecart) (Object) this;
        if (!VanillaFlavorsConfig.get().minecartPush || !self.level().isClientSide() || !(self.getBehavior() instanceof OldMinecartBehavior)) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.level() != self.level() || player.isPassenger() || player.isSpectator()
            || self.hasPassenger(player) || player.noPhysics || self.noPhysics) return;

        // Vagoneta en marcha que choca con el jugador (EntityMinecart.onUpdate -> applyEntityCollision).
        if (self.isRideable() && self.getDeltaMovement().horizontalDistanceSqr() >= 0.01
            && self.getBoundingBox().inflate(0.2, 0.0, 0.2).intersects(player.getBoundingBox())) {
            player.push(self);
        }

        // Jugador pegado a la vagoneta (EntityMinecart.applyEntityCollision): la parte del jugador.
        if (self.getBoundingBox().intersects(player.getBoundingBox())) {
            double xa = player.getX() - self.getX();
            double za = player.getZ() - self.getZ();
            double dd = xa * xa + za * za;
            if (dd >= 1.0E-4F) {
                dd = Math.sqrt(dd);
                double pow = Math.min(1.0 / dd, 1.0);
                xa = xa / dd * pow * 0.1F * 0.5;
                za = za / dd * pow * 0.1F * 0.5;
                player.push(xa / 4.0, 0.0, za / 4.0);
            }
        }
    }

    /** Con el servidor mandando cada tick, 2 pasos de interpolacion bastan y la vagoneta gira al momento. */
    @Inject(method = "createInterpolationHandler", at = @At("RETURN"))
    private void vanillaFlavors$shortInterpolation(CallbackInfoReturnable<InterpolationHandler> cir) {
        if (VanillaFlavorsConfig.get().minecartSync && cir.getReturnValue() instanceof LinearInterpolationHandler linear && Minecraft.getInstance().hasSingleplayerServer()) {
            linear.setInterpolationLength(2);
        }
    }
}
