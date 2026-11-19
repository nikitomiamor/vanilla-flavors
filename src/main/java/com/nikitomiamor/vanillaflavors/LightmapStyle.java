package com.nikitomiamor.vanillaflavors;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Lightmap style as in Better Than Adventure: Grayscale (Beta, the pack's lightmap.fsh)
 * or Colorized (the game's modern lightmap.fsh). Both shaders are compiled at the same time,
 * so it switches instantly, without a resource reload (betaLighting config or the F7 key).
 */
public final class LightmapStyle {
    /** Shader of the colorized pipeline: an empty file in the mod that ShaderManagerMixin swaps for the game's. */
    public static final Identifier COLORIZED_SHADER = Identifier.fromNamespaceAndPath("vanilla_flavors", "core/lightmap_colorized");
    public static final Identifier COLORIZED_SHADER_FILE = Identifier.fromNamespaceAndPath("vanilla_flavors", "shaders/core/lightmap_colorized.fsh");
    public static final Identifier VANILLA_SHADER_FILE = Identifier.withDefaultNamespace("shaders/core/lightmap.fsh");

    /** Created by RenderPipelinesMixin next to the game's pipelines. */
    public static RenderPipeline colorizedPipeline;

    private LightmapStyle() {}

    public static void cycle(Minecraft minecraft) {
        VanillaFlavorsConfig config = VanillaFlavorsConfig.get();
        config.betaLighting = !config.betaLighting;
        VanillaFlavorsConfig.save();
        if (minecraft.player != null) {
            minecraft.player.sendOverlayMessage(Component.translatable("vanilla_flavors.lightmap.changed",
                Component.translatable(config.betaLighting ? "vanilla_flavors.lightmap.gray" : "vanilla_flavors.lightmap.colorized")));
        }
    }
}
