package com.nikitomiamor.classicfixes;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Estilo del lightmap como en Better Than Adventure: Grises (Beta, el lightmap.fsh del pack)
 * o Color (el lightmap.fsh moderno del juego). Los dos shaders estan compilados a la vez,
 * asi que se cambia al instante, sin recargar recursos (config betaLighting o la tecla F7).
 */
public final class LightmapStyle {
    /** Shader del pipeline de color: un archivo vacio del mod que ShaderManagerMixin cambia por el del juego. */
    public static final Identifier COLORIZED_SHADER = Identifier.fromNamespaceAndPath("classic_fixes", "core/lightmap_colorized");
    public static final Identifier COLORIZED_SHADER_FILE = Identifier.fromNamespaceAndPath("classic_fixes", "shaders/core/lightmap_colorized.fsh");
    public static final Identifier VANILLA_SHADER_FILE = Identifier.withDefaultNamespace("shaders/core/lightmap.fsh");

    /** Lo crea RenderPipelinesMixin junto a los pipelines del juego. */
    public static RenderPipeline colorizedPipeline;

    private LightmapStyle() {}

    public static void cycle(Minecraft minecraft) {
        ClassicFixesConfig config = ClassicFixesConfig.get();
        config.betaLighting = !config.betaLighting;
        ClassicFixesConfig.save();
        if (minecraft.player != null) {
            minecraft.player.sendOverlayMessage(Component.translatable("classic_fixes.lightmap.changed",
                Component.translatable(config.betaLighting ? "classic_fixes.lightmap.gray" : "classic_fixes.lightmap.colorized")));
        }
    }
}
