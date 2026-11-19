package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import com.nikitomiamor.vanillaflavors.LoadingTitle;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.ChunkLoadStatusView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Pone "Entering the Nether", "Leaving the End", "Respawning"... encima de "Descargando terreno". */
@Mixin(LevelLoadingScreen.class)
public abstract class LevelLoadingScreenMixin extends Screen {
    @Shadow private LevelLoadTracker loadTracker;

    protected LevelLoadingScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void vanillaFlavors$drawTitle(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        Component title = LoadingTitle.get();
        if (title == null || !VanillaFlavorsConfig.get().loadingTitles) return;
        ChunkLoadStatusView statusView = this.loadTracker.statusView();
        int yCenter = this.height / 2;
        int textTop = statusView != null ? yCenter - statusView.radius() * 2 - 9 * 3 : yCenter - 50;
        graphics.centeredText(this.font, title, this.width / 2, textTop - 9 - 6, -1);
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void vanillaFlavors$clearTitle(CallbackInfo ci) {
        LoadingTitle.clear();
    }
}
