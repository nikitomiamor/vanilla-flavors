package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import com.nikitomiamor.vanillaflavors.LoadingTitle;
import java.util.Set;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    /** Un teleport de otra entidad se aplica de golpe en vez de deslizarla hasta el destino. */
    @ModifyArg(
        method = "handleTeleportEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;setValuesFromPositionPacket(Lnet/minecraft/world/entity/PositionMoveRotation;Ljava/util/Set;Lnet/minecraft/world/entity/Entity;Z)Z"
        ),
        index = 3
    )
    private boolean vanillaFlavors$snapTeleport(PositionMoveRotation change, Set<Relative> relatives, Entity entity, boolean interpolate) {
        if (!VanillaFlavorsConfig.get().instantTeleport) return interpolate;
        return interpolate && entity.isLocalInstanceAuthoritative();
    }

    @Inject(method = "determineLevelLoadingReason", at = @At("HEAD"))
    private void vanillaFlavors$rememberTrip(boolean playerDied, ResourceKey<Level> dimension, ResourceKey<Level> oldDimension,
                                           CallbackInfoReturnable<LevelLoadingScreen.Reason> cir) {
        LoadingTitle.set(playerDied, dimension, oldDimension);
    }

    @Inject(method = "handleLogin", at = @At("HEAD"))
    private void vanillaFlavors$forgetTrip(ClientboundLoginPacket packet, CallbackInfo ci) {
        LoadingTitle.clear();
    }
}
