package com.nikitomiamor.classicfixes.mixin.client;

import com.nikitomiamor.classicfixes.ClickSwing;
import com.nikitomiamor.classicfixes.LightmapStyle;
import com.nikitomiamor.classicfixes.ModKeys;
import com.nikitomiamor.classicfixes.client.ConfigScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    /** Marca los clics de ataque para el golpe de b1.7.3 (ver ClickSwing). */
    @Inject(method = "startAttack", at = @At("HEAD"))
    private void classicFixes$clickStart(CallbackInfoReturnable<Boolean> cir) {
        ClickSwing.active = true;
    }

    @Inject(method = "startAttack", at = @At("RETURN"))
    private void classicFixes$clickEnd(CallbackInfoReturnable<Boolean> cir) {
        ClickSwing.active = false;
    }

    /** Teclas del mod: alternar el estilo del lightmap (como en BTA) y abrir el menu de configuracion. */
    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void classicFixes$modKeys(CallbackInfo ci) {
        Minecraft minecraft = (Minecraft) (Object) this;
        while (ModKeys.CYCLE_LIGHTMAP.consumeClick()) {
            LightmapStyle.cycle(minecraft);
        }
        while (ModKeys.OPEN_CONFIG.consumeClick()) {
            if (minecraft.gui.screen() == null && FabricLoader.getInstance().isModLoaded("cloth-config")) {
                minecraft.gui.setScreen(ConfigScreen.create(null));
            }
        }
    }
}
