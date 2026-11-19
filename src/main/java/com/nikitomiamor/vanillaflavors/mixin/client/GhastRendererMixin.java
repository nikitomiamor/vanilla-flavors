package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import com.nikitomiamor.vanillaflavors.GhastCharge;
import com.nikitomiamor.vanillaflavors.GhastSwellAccess;
import com.nikitomiamor.vanillaflavors.GhastSwellState;
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

/** The ghast swells before spitting the fireball, as until 12w17a. */
@Mixin(GhastRenderer.class)
public abstract class GhastRendererMixin {
    @Inject(
        method = "extractRenderState(Lnet/minecraft/world/entity/monster/Ghast;Lnet/minecraft/client/renderer/entity/state/GhastRenderState;F)V",
        at = @At("TAIL")
    )
    private void vanillaFlavors$swell(Ghast ghast, GhastRenderState state, float partialTicks, CallbackInfo ci) {
        if (!VanillaFlavorsConfig.get().ghastSwell) {
            ((GhastSwellState) state).vanillaFlavors$setScale(1.0F, 1.0F);
            return;
        }
        GhastSwellAccess access = (GhastSwellAccess) ghast;
        if (ghast.tickCount != access.vanillaFlavors$lastTick()) {
            access.vanillaFlavors$advance(ghast.tickCount, vanillaFlavors$sample(ghast, access.vanillaFlavors$cur()));
        }
        float counter = Mth.lerp(partialTicks, access.vanillaFlavors$prev(), access.vanillaFlavors$cur());
        float[] scale = GhastCharge.scale(counter);
        ((GhastSwellState) state).vanillaFlavors$setScale(scale[0], scale[1]);
    }

    /**
     * In singleplayer it reads the real counter from the integrated server. In multiplayer only
     * "charging yes/no" arrives (it turns on at tick 11 and fires at 20), so we imitate it.
     */
    @Unique
    private static int vanillaFlavors$sample(Ghast ghast, float current) {
        if (Minecraft.getInstance().hasSingleplayerServer()) {
            return GhastCharge.SERVER.getOrDefault(ghast.getUUID(), 0);
        }
        int cur = (int) current;
        if (ghast.isCharging()) return cur < 11 ? 11 : Math.min(cur + 1, 19);
        return cur >= 19 ? -40 : 0;
    }
}
