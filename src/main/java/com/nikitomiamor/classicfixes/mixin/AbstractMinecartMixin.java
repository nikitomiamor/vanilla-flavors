package com.nikitomiamor.classicfixes.mixin;

import com.nikitomiamor.classicfixes.ClassicFixesConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** En un jugador, manda la posicion de la vagoneta cada tick (no cada 3) para que el cliente no la retrase. */
@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void classicFixes$syncEveryTick(CallbackInfo ci) {
        AbstractMinecart self = (AbstractMinecart) (Object) this;
        if (ClassicFixesConfig.get().minecartSync
            && self.level() instanceof ServerLevel level
            && !level.getServer().isDedicatedServer()
            && self.getBehavior() instanceof OldMinecartBehavior) {
            self.needsSync = true;
        }
    }
}
