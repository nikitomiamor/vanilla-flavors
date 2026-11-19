package com.nikitomiamor.vanillaflavors.mixin.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.nikitomiamor.vanillaflavors.LightmapStyle;
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
 * The colorized lightmap (vanilla_flavors:core/lightmap_colorized) is a placeholder file in the mod;
 * when it is loaded the game's original lightmap.fsh is used, even if the pack has its own grayscale one.
 * That way it always matches the game version without copying its code.
 */
@Mixin(ShaderManager.class)
public abstract class ShaderManagerMixin {
    @ModifyVariable(method = "loadShader", at = @At("HEAD"), argsOnly = true)
    private static Resource vanillaFlavors$vanillaLightmap(Resource resource, Identifier id, Resource original, ShaderType type,
                                                         ImmutableMap.Builder<?, String> builder) {
        if (!LightmapStyle.COLORIZED_SHADER_FILE.equals(id)) return resource;
        PackResources vanilla = Minecraft.getInstance().getVanillaPackResources().fullResources();
        IoSupplier<InputStream> stream = vanilla.getResource(PackType.CLIENT_RESOURCES, LightmapStyle.VANILLA_SHADER_FILE);
        return stream == null ? resource : new Resource(vanilla, stream);
    }
}
