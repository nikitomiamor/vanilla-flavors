package com.nikitomiamor.vanillaflavors.mixin;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import com.nikitomiamor.vanillaflavors.CreativeKit;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    /** Empuje sin direccion (xd = zd = 0): lado al azar en vez de un angulo sin sentido. */
    @Inject(method = "indicateDamage", at = @At("HEAD"), cancellable = true)
    private void vanillaFlavors$randomTilt(double xd, double zd, CallbackInfo ci) {
        if (xd == 0.0 && zd == 0.0 && VanillaFlavorsConfig.get().randomDamageTilt) {
            ServerPlayer self = (ServerPlayer) (Object) this;
            ((PlayerAccessor) self).vanillaFlavors$setHurtDir((int) (Math.random() * 2.0) * 180.0F);
            self.connection.send(new ClientboundHurtAnimationPacket(self));
            ci.cancel();
        }
    }

    @Inject(method = "setGameMode", at = @At("RETURN"))
    private void vanillaFlavors$creativeKitOnSwitch(GameType mode, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && mode == GameType.CREATIVE) CreativeKit.give((ServerPlayer) (Object) this);
    }
}
