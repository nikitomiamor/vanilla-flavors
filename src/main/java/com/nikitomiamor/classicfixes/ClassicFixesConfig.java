package com.nikitomiamor.classicfixes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Interruptores de cada arreglo, guardados en config/classic_fixes.json.
 * Todos activados por defecto. La pantalla de Mod Menu recorre estos campos por reflexion,
 * asi que cada boolean nuevo necesita sus claves classic_fixes.config.<campo> en los idiomas
 * y un @Group (pestana del menu: graphics, animations, entities o gameplay; sin el va a "other").
 */
public final class ClassicFixesConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("classic_fixes.json");
    private static ClassicFixesConfig instance = load();

    @Group("entities")
    public boolean ghastSwell = true;
    @Group("entities")
    public boolean minecartPush = true;
    @Group("entities")
    public boolean minecartSync = true;
    @Group("gameplay")
    public boolean instantTeleport = true;
    @Group("gameplay")
    public boolean creativeKit = true;
    @Group("gameplay")
    public boolean notchApple = true;
    @Group("graphics")
    public boolean golemParticles = true;
    @Group("gameplay")
    public boolean mapMarkerEveryTick = true;
    @Group("animations")
    public boolean randomDamageTilt = true;
    @Group("graphics")
    public boolean loadingTitles = true;
    @Group("animations")
    public boolean sidewaysBackwardWalk = true;
    @Group("animations")
    public boolean classicHeadTurn = true;
    @Group("animations")
    public boolean twistedArm = true;
    @Group("animations")
    public boolean fallTilt = true;
    @Group("graphics")
    public boolean voidParticles = true;
    @Group("graphics")
    public boolean betaLighting = true;
    @Group("graphics")
    public boolean oldSmoothLighting = true;
    @Group("animations")
    public boolean oldArmSwing = true;
    @Group("animations")
    public boolean noArmSway = true;
    @Group("graphics")
    public boolean betaFog = true;

    /** Pestana del menu de configuracion (classic_fixes.config.group.<nombre>). */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Group {
        String value();
    }

    public static ClassicFixesConfig get() {
        return instance;
    }

    private static ClassicFixesConfig load() {
        ClassicFixesConfig config = null;
        if (Files.exists(FILE)) {
            try (Reader reader = Files.newBufferedReader(FILE)) {
                config = GSON.fromJson(reader, ClassicFixesConfig.class);
            } catch (Exception e) {
                System.err.println("[Classic Fixes] No se pudo leer " + FILE + ": " + e);
            }
        }
        if (config == null) config = new ClassicFixesConfig();
        instance = config;
        save(); // escribe los campos nuevos que falten en el archivo
        return config;
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                GSON.toJson(instance, writer);
            }
        } catch (Exception e) {
            System.err.println("[Classic Fixes] No se pudo guardar " + FILE + ": " + e);
        }
    }
}
