package com.nikitomiamor.classicfixes.mixin.client;

import com.nikitomiamor.classicfixes.ClassicFixesConfig;
import com.nikitomiamor.classicfixes.GhastCharge;
import com.nikitomiamor.classicfixes.GhastSwellAccess;
import com.nikitomiamor.classicfixes.GhastSwellState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.client.renderer.entity.state.GhastRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Ghast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** El ghast se hincha antes de escupir la bola de fuego, como hasta 12w17a. */
@Mixin(GhastRenderer.class)
public abstract class GhastRendererMixin {
    @Inject(
        method = "extractRenderState(Lnet/minecraft/world/entity/monster/Ghast;Lnet/minecraft/client/renderer/entity/state/GhastRenderState;F)V",
        at = @At("TAIL")
    )
    private void classicFixes$swell(Ghast ghast, GhastRenderState state, float partialTicks, CallbackInfo ci) {
        if (!ClassicFixesConfig.get().ghastSwell) {
            ((GhastSwellState) state).classicFixes$setScale(1.0F, 1.0F);
            return;
        }
        GhastSwellAccess access = (GhastSwellAccess) ghast;
        if (ghast.tickCount != access.classicFixes$lastTick()) {
            access.classicFixes$advance(ghast.tickCount, classicFixes$sample(ghast, access.classicFixes$cur()));
        }
        float counter = Mth.lerp(partialTicks, access.classicFixes$prev(), access.classicFixes$cur());
        float[] scale = GhastCharge.scale(counter);
        ((GhastSwellState) state).classicFixes$setScale(scale[0], scale[1]);
    }

    /**
     * En un jugador lee el contador real del servidor integrado. En multijugador solo llega
     * "cargando si/no" (que se activa en el tick 11 y dispara en el 20), asi que lo imitamos.
     */
    @Unique
    private static int classicFixes$sample(Ghast ghast, float current) {
        if (Minecraft.getInstance().hasSingleplayerServer()) {
            return GhastCharge.SERVER.getOrDefault(ghast.getUUID(), 0);
        }
        int cur = (int) current;
        if (ghast.isCharging()) return cur < 11 ? 11 : Math.min(cur + 1, 19);
        return cur >= 19 ? -40 : 0;
    }
}
