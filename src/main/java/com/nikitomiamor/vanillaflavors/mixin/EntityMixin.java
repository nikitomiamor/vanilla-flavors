package com.nikitomiamor.vanillaflavors.mixin;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import java.util.Set;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PositionMoveRotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Endermen (and everything that uses teleportTo) move with a normal packet and the client
 * slides them to the destination. We send a teleport packet instead, which the client applies at once.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "teleportTo(DDD)V", at = @At("TAIL"))
    private void vanillaFlavors$instantTeleport(double x, double y, double z, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (!VanillaFlavorsConfig.get().instantTeleport || self instanceof ServerPlayer || self.isPassenger()) return;
        if (self.level() instanceof ServerLevel level) {
            level.getChunkSource().sendToTrackingPlayers(self,
                ClientboundTeleportEntityPacket.teleport(self.getId(), PositionMoveRotation.of(self), Set.of(), self.onGround()));
        }
    }
}
