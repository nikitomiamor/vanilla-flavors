package com.nikitomiamor.vanillaflavors;

/**
 * true solo mientras Minecraft.startAttack (un clic izquierdo) esta en marcha en el hilo del cliente.
 * Asi el golpe de b1.7.3 reinicia la animacion en cada clic, pero no en los swing() que el juego
 * llama cada tick al mantener el boton minando (eso dejaba el brazo quieto).
 */
public final class ClickSwing {
    public static boolean active;

    private ClickSwing() {}
}
