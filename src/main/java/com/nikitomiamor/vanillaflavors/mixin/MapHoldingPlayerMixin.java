package com.nikitomiamor.vanillaflavors.mixin;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/** The map marker was sent every 5 ticks (4 times per second); now every tick. */
@Mixin(targets = "net.minecraft.world.level.saveddata.maps.MapItemSavedData$HoldingPlayer")
public abstract class MapHoldingPlayerMixin {
    @ModifyConstant(method = "nextUpdatePacket", constant = @Constant(intValue = 5))
    private int vanillaFlavors$everyTick(int original) {
        return VanillaFlavorsConfig.get().mapMarkerEveryTick ? 1 : original;
    }
}
