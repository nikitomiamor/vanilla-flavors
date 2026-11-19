package com.nikitomiamor.classicfixes.mixin.client;

import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.nikitomiamor.classicfixes.LightmapStyle;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Registra una copia del pipeline LIGHTMAP con el shader moderno (classic_fixes:core/lightmap_colorized).
 * Registrado aqui, el juego lo compila en cada recarga junto a los suyos.
 */
@Mixin(RenderPipelines.class)
public abstract class RenderPipelinesMixin {
    @Shadow @Final private static RenderPipeline.Snippet GLOBALS_SNIPPET;

    @Shadow
    private static RenderPipeline register(RenderPipeline pipeline) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void classicFixes$colorizedLightmap(CallbackInfo ci) {
        LightmapStyle.colorizedPipeline = register(RenderPipeline.builder(GLOBALS_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("classic_fixes", "pipeline/lightmap_colorized"))
            .withVertexShader("core/screenquad")
            .withFragmentShader(LightmapStyle.COLORIZED_SHADER)
            .withBindGroupLayout(BindGroupLayouts.LIGHTMAP_INFO)
            .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
            .withColorTargetState(ColorTargetState.DEFAULT)
            .build());
    }
}
