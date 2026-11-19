package com.nikitomiamor.vanillaflavors.mixin;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.pattern.BlockPattern.BlockPatternMatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Particulas de construccion de 1.2.5 (BlockPumpkin.onBlockAdded): 120 copos o bolas de nieve. */
@Mixin(CarvedPumpkinBlock.class)
public abstract class CarvedPumpkinBlockMixin {
    @Inject(method = "spawnGolemInWorld", at = @At("TAIL"))
    private static void vanillaFlavors$golemParticles(Level level, BlockPatternMatch match, Entity golem, BlockPos pos, CallbackInfo ci) {
        if (!VanillaFlavorsConfig.get().golemParticles || !(level instanceof ServerLevel server)) return;
        SimpleParticleType type;
        double height;
        if (golem instanceof SnowGolem) {
            type = ParticleTypes.SNOWFLAKE;
            height = 2.5;
        } else if (golem instanceof IronGolem) {
            type = ParticleTypes.ITEM_SNOWBALL;
            height = 3.9;
        } else {
            return;
        }
        RandomSource random = server.getRandom();
        for (int i = 0; i < 120; i++) {
            server.sendParticles(type, pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble() * height,
                pos.getZ() + random.nextDouble(), 0, 0.0, 0.0, 0.0, 0.0);
        }
    }
}
