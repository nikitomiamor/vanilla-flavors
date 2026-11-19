package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.GhastSwellState;
import net.minecraft.client.renderer.entity.state.GhastRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GhastRenderState.class)
public abstract class GhastRenderStateMixin implements GhastSwellState {
    @Unique private float vanillaFlavors$scaleXZ = 1.0F;
    @Unique private float vanillaFlavors$scaleY = 1.0F;

    @Override public float vanillaFlavors$scaleXZ() { return this.vanillaFlavors$scaleXZ; }
    @Override public float vanillaFlavors$scaleY() { return this.vanillaFlavors$scaleY; }

    @Override
    public void vanillaFlavors$setScale(float xz, float y) {
        this.vanillaFlavors$scaleXZ = xz;
        this.vanillaFlavors$scaleY = y;
    }
}
