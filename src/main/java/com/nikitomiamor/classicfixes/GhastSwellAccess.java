package com.nikitomiamor.classicfixes;

/** Estado del lado cliente que se guarda en cada ghast. */
public interface GhastSwellAccess {
    int classicFixes$lastTick();
    float classicFixes$prev();
    float classicFixes$cur();
    void classicFixes$advance(int tick, float value);
}
