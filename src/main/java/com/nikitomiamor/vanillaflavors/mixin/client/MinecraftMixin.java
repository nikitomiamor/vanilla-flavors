package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.ClickSwing;
import com.nikitomiamor.vanillaflavors.LightmapStyle;
import com.nikitomiamor.vanillaflavors.ModKeys;
import com.nikitomiamor.vanillaflavors.client.ConfigScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    /** Marks attack clicks for the b1.7.3 swing (see ClickSwing). */
    @Inject(method = "startAttack", at = @At("HEAD"))
    private void vanillaFlavors$clickStart(CallbackInfoReturnable<Boolean> cir) {
        ClickSwing.active = true;
    }

    @Inject(method = "startAttack", at = @At("RETURN"))
    private void vanillaFlavors$clickEnd(CallbackInfoReturnable<Boolean> cir) {
        ClickSwing.active = false;
    }

    /** The mod's keys: toggle the lightmap style (as in BTA) and open the config menu. */
    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void vanillaFlavors$modKeys(CallbackInfo ci) {
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
