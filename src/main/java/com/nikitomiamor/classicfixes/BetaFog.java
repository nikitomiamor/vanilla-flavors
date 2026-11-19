package com.nikitomiamor.classicfixes;

/** Estado compartido entre los mixins de la niebla (solo hilo de render). */
public final class BetaFog {
    /** true si en el fotograma actual se aplico la niebla del aire. */
    public static boolean atmospheric;

    private BetaFog() {}
}
