package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Void particles (WorldClient.doVoidFogParticles from 1.7/1.8, removed in 1.8), without the fog.
 * Every tick: 1000 attempts within +-15 blocks of the player; in air, with a chance that grows closer
 * to the bottom of the world (rand(8) > height above the bottom), a still grey dot appears ("depthsuspend").
 * voidParticlesMaxY stretches that fade upward; taller ranges thin out so the particle count stays about the same.
 * The mycelium particle (SuspendedTownParticle) is the same class depthsuspend used (EntityAuraFX).
 * Only in dimensions with a sky and not in superflat worlds, as before.
 */
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
    @Inject(method = "animateTick", at = @At("TAIL"))
    private void vanillaFlavors$voidParticles(int x, int y, int z, CallbackInfo ci) {
        if (!VanillaFlavorsConfig.get().voidParticles) return;
        ClientLevel level = (ClientLevel) (Object) this;
        int minY = level.getMinY();
        int range = Math.max(8, VanillaFlavorsConfig.get().voidParticlesMaxY - minY + 2);
        if (y - minY > range + 16) return; // no attempt can land in the particle layers
        if (!level.dimensionType().hasSkyLight()
            || ((ClientLevelDataAccessor) level.getLevelData()).vanillaFlavors$isFlat()) return;

        RandomSource random = level.getRandom();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 1000; i++) {
            int px = x + random.nextInt(16) - random.nextInt(16);
            int py = y + random.nextInt(16) - random.nextInt(16);
            int pz = z + random.nextInt(16) - random.nextInt(16);
            if (random.nextInt(range) > py - minY && random.nextInt(range) < 8 && level.getBlockState(pos.set(px, py, pz)).isAir()) {
                level.addParticle(ParticleTypes.MYCELIUM,
                    px + random.nextFloat(), py + random.nextFloat(), pz + random.nextFloat(), 0.0, 0.0, 0.0);
            }
        }
    }
}
