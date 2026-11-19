package com.nikitomiamor.classicfixes;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Ghast;

/**
 * Antes de 12w18a el cliente leia el contador de disparo del ghast y lo hinchaba.
 * El servidor nunca lo envia, asi que en un jugador el servidor integrado lo deja aqui
 * (por UUID, que es igual en cliente y servidor) y el renderer lo lee.
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

    /** Escala de 1.2.5 (RenderGhast.preRenderCallback), relativa al 4.5 que ya trae el modelo. */
    public static float[] scale(float counter) {
        float t = counter / 20.0F;
        if (t < 0.0F) t = 0.0F;
        float f = 1.0F / (t * t * t * t * t * 2.0F + 1.0F);
        float y = (8.0F + f) / 2.0F / 4.5F;
        float xz = (8.0F + 1.0F / f) / 2.0F / 4.5F;
        return new float[] {xz, y};
    }
}
