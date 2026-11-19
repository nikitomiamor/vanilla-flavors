package com.nikitomiamor.vanillaflavors;

/**
 * true only while Minecraft.startAttack (a left click) is running on the client thread.
 * This way the b1.7.3 swing restarts the animation on every click, but not on the swing() calls the game
 * makes every tick while the button is held to mine (that froze the arm).
 */
public final class ClickSwing {
    public static boolean active;

    private ClickSwing() {}
}
