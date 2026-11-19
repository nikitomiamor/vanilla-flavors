package com.nikitomiamor.classicfixes.mixin.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.nikitomiamor.classicfixes.LightmapStyle;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * El lightmap de color (classic_fixes:core/lightmap_colorized) es un archivo de relleno en el mod;
 * al cargarlo se usa el lightmap.fsh original del juego, aunque el pack tenga el suyo de grises.
 * Asi siempre coincide con la version del juego sin copiar su codigo.
 */
@Mixin(ShaderManager.class)
public abstract class ShaderManagerMixin {
    @ModifyVariable(method = "loadShader", at = @At("HEAD"), argsOnly = true)
    private static Resource classicFixes$vanillaLightmap(Resource resource, Identifier id, Resource original, ShaderType type,
                                                         ImmutableMap.Builder<?, String> builder) {
        if (!LightmapStyle.COLORIZED_SHADER_FILE.equals(id)) return resource;
        PackResources vanilla = Minecraft.getInstance().getVanillaPackResources().fullResources();
        IoSupplier<InputStream> stream = vanilla.getResource(PackType.CLIENT_RESOURCES, LightmapStyle.VANILLA_SHADER_FILE);
        return stream == null ? resource : new Resource(vanilla, stream);
    }
}
