package com.nikitomiamor.classicfixes.mixin.client;

import com.nikitomiamor.classicfixes.GhastSwellState;
import net.minecraft.client.renderer.entity.state.GhastRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GhastRenderState.class)
public abstract class GhastRenderStateMixin implements GhastSwellState {
    @Unique private float classicFixes$scaleXZ = 1.0F;
    @Unique private float classicFixes$scaleY = 1.0F;

    @Override public float classicFixes$scaleXZ() { return this.classicFixes$scaleXZ; }
    @Override public float classicFixes$scaleY() { return this.classicFixes$scaleY; }

    @Override
    public void classicFixes$setScale(float xz, float y) {
        this.classicFixes$scaleXZ = xz;
        this.classicFixes$scaleY = y;
    }
}
