package com.nikitomiamor.vanillaflavors.mixin;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** In singleplayer, sends the minecart position every tick (not every 3) so the client does not lag behind. */
@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void vanillaFlavors$syncEveryTick(CallbackInfo ci) {
        AbstractMinecart self = (AbstractMinecart) (Object) this;
        if (VanillaFlavorsConfig.get().minecartSync
            && self.level() instanceof ServerLevel level
            && !level.getServer().isDedicatedServer()
            && self.getBehavior() instanceof OldMinecartBehavior) {
            self.needsSync = true;
        }
    }
}
