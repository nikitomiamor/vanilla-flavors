package com.nikitomiamor.vanillaflavors;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Ghast;

/**
 * Before 12w18a the client read the ghast's attack counter and swelled it.
 * The server never sends it, so in singleplayer the integrated server stores it here
 * (by UUID, which is the same on client and server) and the renderer reads it.
 */
public final class GhastCharge {
    public static final Map<UUID, Integer> SERVER = new ConcurrentHashMap<>();

    private GhastCharge() {}

    public static void publish(Ghast ghast, int chargeTime) {
        if (!(ghast.level() instanceof ServerLevel level) || level.getServer().isDedicatedServer()) return;
        if (chargeTime == 0) SERVER.remove(ghast.getUUID());
        else SERVER.put(ghast.getUUID(), chargeTime);
    }

    public static void clear(Ghast ghast) {
        SERVER.remove(ghast.getUUID());
    }

    /** 1.2.5 scale (RenderGhast.preRenderCallback), relative to the 4.5 the model already has. */
    public static float[] scale(float counter) {
        float t = counter / 20.0F;
        if (t < 0.0F) t = 0.0F;
        float f = 1.0F / (t * t * t * t * t * 2.0F + 1.0F);
        float y = (8.0F + f) / 2.0F / 4.5F;
        float xz = (8.0F + 1.0F / f) / 2.0F / 4.5F;
        return new float[] {xz, y};
    }
}
