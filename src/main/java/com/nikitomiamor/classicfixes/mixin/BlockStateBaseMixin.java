package com.nikitomiamor.classicfixes.mixin;

import com.nikitomiamor.classicfixes.ClassicFixesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Luz suave de b1.7.3 (RenderBlocks.renderStandardBlockWithAmbientOcclusion): cada esquina era la media del brillo
 * de sus 4 vecinos, y un bloque opaco aportaba su propio brillo, el de luz 0 (0.05). Ahora la media es de niveles
 * de luz (los opacos no cuentan) por una sombra de 0.2 para todo bloque con colision entera (cristal y hojas incluidos).
 * Dando 0.05 a los bloques que tapan la luz y 1.0 al resto sale la misma sombra que en b1.7.3.
 * Sodium lee este mismo metodo (LightDataAccess), asi que vale con y sin Sodium.
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Inject(method = "getShadeBrightness", at = @At("HEAD"), cancellable = true)
    private void classicFixes$betaAmbientOcclusion(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (!ClassicFixesConfig.get().oldSmoothLighting) return;
        BlockBehaviour.BlockStateBase state = (BlockBehaviour.BlockStateBase) (Object) this;
        boolean opaque = state.getLightDampening() >= 15 && state.getLightEmission() == 0;
        cir.setReturnValue(opaque ? 0.05F : 1.0F);
    }
}
