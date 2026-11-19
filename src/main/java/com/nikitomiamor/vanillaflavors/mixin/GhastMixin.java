package com.nikitomiamor.vanillaflavors.mixin;

import com.nikitomiamor.vanillaflavors.GhastSwellAccess;
import net.minecraft.world.entity.monster.Ghast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Ghast.class)
public abstract class GhastMixin implements GhastSwellAccess {
    @Unique private int vanillaFlavors$lastTick = Integer.MIN_VALUE;
    @Unique private float vanillaFlavors$prev;
    @Unique private float vanillaFlavors$cur;

    @Override public int vanillaFlavors$lastTick() { return this.vanillaFlavors$lastTick; }
    @Override public float vanillaFlavors$prev() { return this.vanillaFlavors$prev; }
    @Override public float vanillaFlavors$cur() { return this.vanillaFlavors$cur; }

    @Override
    public void vanillaFlavors$advance(int tick, float value) {
        this.vanillaFlavors$prev = this.vanillaFlavors$lastTick == Integer.MIN_VALUE ? value : this.vanillaFlavors$cur;
        this.vanillaFlavors$cur = value;
        this.vanillaFlavors$lastTick = tick;
    }
}
