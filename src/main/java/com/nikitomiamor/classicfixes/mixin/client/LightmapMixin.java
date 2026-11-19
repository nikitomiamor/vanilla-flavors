package com.nikitomiamor.classicfixes.mixin.client;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.nikitomiamor.classicfixes.ClassicFixesConfig;
import com.nikitomiamor.classicfixes.LightmapStyle;
import net.minecraft.client.renderer.Lightmap;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Elige el lightmap en cada actualizacion (cada tick): grises del pack o color del juego. */
@Mixin(Lightmap.class)
public abstract class LightmapMixin {
    @Redirect(method = "render", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC,
        target = "Lnet/minecraft/client/renderer/RenderPipelines;LIGHTMAP:Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;"))
    private RenderPipeline classicFixes$pickLightmap() {
        return ClassicFixesConfig.get().betaLighting || LightmapStyle.colorizedPipeline == null
            ? net.minecraft.client.renderer.RenderPipelines.LIGHTMAP
            : LightmapStyle.colorizedPipeline;
    }
}
