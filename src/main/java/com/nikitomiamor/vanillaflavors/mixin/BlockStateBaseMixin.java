package com.nikitomiamor.vanillaflavors.mixin;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * b1.7.3 smooth lighting (RenderBlocks.renderStandardBlockWithAmbientOcclusion): each corner was the average brightness
 * of its 4 neighbours, and an opaque block contributed its own brightness, that of light 0 (0.05). Now the average is of light
 * levels (opaque blocks do not count) times a 0.2 shade for every block with a full collision box (glass and leaves included).
 * Giving 0.05 to blocks that block light and 1.0 to the rest produces the same shading as b1.7.3.
 * Sodium reads this same method (LightDataAccess), so it works with and without Sodium.
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Inject(method = "getShadeBrightness", at = @At("HEAD"), cancellable = true)
    private void vanillaFlavors$betaAmbientOcclusion(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (!VanillaFlavorsConfig.get().oldSmoothLighting) return;
        BlockBehaviour.BlockStateBase state = (BlockBehaviour.BlockStateBase) (Object) this;
        boolean opaque = state.getLightDampening() >= 15 && state.getLightEmission() == 0;
        cir.setReturnValue(opaque ? 0.05F : 1.0F);
    }
}
