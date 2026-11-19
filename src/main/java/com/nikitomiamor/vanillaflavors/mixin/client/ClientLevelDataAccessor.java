package com.nikitomiamor.vanillaflavors.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientLevel.ClientLevelData.class)
public interface ClientLevelDataAccessor {
    @Accessor("isFlat")
    boolean vanillaFlavors$isFlat();
}
