package com.nikitomiamor.classicfixes.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Player.class)
public interface PlayerAccessor {
    @Accessor("hurtDir")
    void classicFixes$setHurtDir(float hurtDir);
}
