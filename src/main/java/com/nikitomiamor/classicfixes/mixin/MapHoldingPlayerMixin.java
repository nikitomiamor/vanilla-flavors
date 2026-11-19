package com.nikitomiamor.classicfixes.mixin;

import com.nikitomiamor.classicfixes.ClassicFixesConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/** El marcador del mapa se mandaba cada 5 ticks (4 veces por segundo); ahora cada tick. */
@Mixin(targets = "net.minecraft.world.level.saveddata.maps.MapItemSavedData$HoldingPlayer")
public abstract class MapHoldingPlayerMixin {
    @ModifyConstant(method = "nextUpdatePacket", constant = @Constant(intValue = 5))
    private int classicFixes$everyTick(int original) {
        return ClassicFixesConfig.get().mapMarkerEveryTick ? 1 : original;
    }
}
