package com.nikitomiamor.vanillaflavors.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.SwingState.class)
public interface SwingStateAccessor {
    @Accessor("currentSwing")
    void vanillaFlavors$setCurrentSwing(LivingEntity.SwingDescription swing);
}
