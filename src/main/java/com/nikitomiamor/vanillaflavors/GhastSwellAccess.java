package com.nikitomiamor.vanillaflavors;

/** Client-side state stored on each ghast. */
public interface GhastSwellAccess {
    int vanillaFlavors$lastTick();
    float vanillaFlavors$prev();
    float vanillaFlavors$cur();
    void vanillaFlavors$advance(int tick, float value);
}
