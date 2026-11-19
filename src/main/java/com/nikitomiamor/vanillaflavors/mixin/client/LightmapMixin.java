package com.nikitomiamor.vanillaflavors.mixin.client;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import com.nikitomiamor.vanillaflavors.LightmapStyle;
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
    private RenderPipeline vanillaFlavors$pickLightmap() {
        return VanillaFlavorsConfig.get().betaLighting || LightmapStyle.colorizedPipeline == null
            ? net.minecraft.client.renderer.RenderPipelines.LIGHTMAP
            : LightmapStyle.colorizedPipeline;
    }
}
