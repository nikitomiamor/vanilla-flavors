package com.nikitomiamor.classicfixes.mixin;

import com.nikitomiamor.classicfixes.ClassicFixesConfig;
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
 * Los endermen (y todo lo que usa teleportTo) se mueven con un paquete normal y el cliente
 * los desliza hasta el destino. Mandamos un paquete de teleport, que el cliente aplica de golpe.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "teleportTo(DDD)V", at = @At("TAIL"))
    private void classicFixes$instantTeleport(double x, double y, double z, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (!ClassicFixesConfig.get().instantTeleport || self instanceof ServerPlayer || self.isPassenger()) return;
        if (self.level() instanceof ServerLevel level) {
            level.getChunkSource().sendToTrackingPlayers(self,
                ClientboundTeleportEntityPacket.teleport(self.getId(), PositionMoveRotation.of(self), Set.of(), self.onGround()));
        }
    }
}
