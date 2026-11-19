package com.nikitomiamor.classicfixes.mixin;

import com.nikitomiamor.classicfixes.GhastSwellAccess;
import net.minecraft.world.entity.monster.Ghast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Ghast.class)
public abstract class GhastMixin implements GhastSwellAccess {
    @Unique private int classicFixes$lastTick = Integer.MIN_VALUE;
    @Unique private float classicFixes$prev;
    @Unique private float classicFixes$cur;

    @Override public int classicFixes$lastTick() { return this.classicFixes$lastTick; }
    @Override public float classicFixes$prev() { return this.classicFixes$prev; }
    @Override public float classicFixes$cur() { return this.classicFixes$cur; }

    @Override
    public void classicFixes$advance(int tick, float value) {
        this.classicFixes$prev = this.classicFixes$lastTick == Integer.MIN_VALUE ? value : this.classicFixes$cur;
        this.classicFixes$cur = value;
        this.classicFixes$lastTick = tick;
    }
}
