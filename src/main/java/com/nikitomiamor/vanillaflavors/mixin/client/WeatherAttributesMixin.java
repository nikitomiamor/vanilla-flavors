package com.nikitomiamor.vanillaflavors.mixin.client;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.attribute.WeatherAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Before 25w45a (1.21.11) rain and thunder did not change the sunrise and sunset color. */
@Mixin(WeatherAttributes.class)
public abstract class WeatherAttributesMixin {
    @Inject(method = "lambda$addLayer$0", at = @At("HEAD"), cancellable = true)
    private static void vanillaFlavors$clearSunset(WeatherAttributes.WeatherAccess weather, EnvironmentAttributeMap.Entry<?, ?> rain,
                                                   EnvironmentAttribute<?> attribute, EnvironmentAttributeMap.Entry<?, ?> thunder,
                                                   Object value, int time, CallbackInfoReturnable<Object> cir) {
        if (attribute == EnvironmentAttributes.SUNRISE_SUNSET_COLOR && VanillaFlavorsConfig.get().sunsetIgnoresWeather) {
            cir.setReturnValue(value);
        }
    }
}
