package com.nikitomiamor.classicfixes.mixin.client;

import com.nikitomiamor.classicfixes.ClassicFixesConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Particulas del vacio (WorldClient.doVoidFogParticles de 1.7/1.8, quitadas en 1.8), sin la niebla.
 * Cada tick: 1000 intentos a +-15 bloques del jugador; en aire, con probabilidad que crece al acercarse
 * al fondo del mundo (rand(8) > altura sobre el fondo), sale un puntito gris quieto ("depthsuspend").
 * La particula del micelio (SuspendedTownParticle) es la misma clase que usaba depthsuspend (EntityAuraFX).
 * Solo en dimensiones con cielo y no en mundos planos, como antes.
 */
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
    @Inject(method = "animateTick", at = @At("TAIL"))
    private void classicFixes$voidParticles(int x, int y, int z, CallbackInfo ci) {
        if (!ClassicFixesConfig.get().voidParticles) return;
        ClientLevel level = (ClientLevel) (Object) this;
        int minY = level.getMinY();
        if (y - minY > 24) return; // ningun intento puede caer en las 7 capas del fondo
        if (!level.dimensionType().hasSkyLight()
            || ((ClientLevelDataAccessor) level.getLevelData()).classicFixes$isFlat()) return;

        RandomSource random = level.getRandom();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 1000; i++) {
            int px = x + random.nextInt(16) - random.nextInt(16);
            int py = y + random.nextInt(16) - random.nextInt(16);
            int pz = z + random.nextInt(16) - random.nextInt(16);
            if (random.nextInt(8) > py - minY && level.getBlockState(pos.set(px, py, pz)).isAir()) {
                level.addParticle(ParticleTypes.MYCELIUM,
                    px + random.nextFloat(), py + random.nextFloat(), pz + random.nextFloat(), 0.0, 0.0, 0.0);
            }
        }
    }
}
