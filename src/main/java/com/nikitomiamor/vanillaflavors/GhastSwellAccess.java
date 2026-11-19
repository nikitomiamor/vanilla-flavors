package com.nikitomiamor.vanillaflavors;

/** Estado del lado cliente que se guarda en cada ghast. */
public interface GhastSwellAccess {
    int vanillaFlavors$lastTick();
    float vanillaFlavors$prev();
    float vanillaFlavors$cur();
    void vanillaFlavors$advance(int tick, float value);
}
