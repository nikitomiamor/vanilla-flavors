package com.nikitomiamor.classicfixes.client;

import com.nikitomiamor.classicfixes.ClassicFixesConfig;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Un interruptor por cada campo boolean de ClassicFixesConfig, repartidos en pestanas segun su @Group.
 * Las pestanas salen en el orden de GROUPS; dentro de cada una, en el orden en que estan declarados los campos.
 */
public final class ConfigScreen {
    private static final String[] GROUPS = {"graphics", "animations", "entities", "gameplay", "other"};

    private ConfigScreen() {}

    public static Screen create(Screen parent) {
        ClassicFixesConfig config = ClassicFixesConfig.get();
        ClassicFixesConfig defaults = new ClassicFixesConfig();
        boolean aoBefore = config.oldSmoothLighting;
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.translatable("classic_fixes.config.title"))
            .setSavingRunnable(() -> {
                ClassicFixesConfig.save();
                // la luz suave (mallas de chunks) solo cambia al recargar; el estilo del lightmap cambia al instante
                if (config.oldSmoothLighting != aoBefore) Minecraft.getInstance().reloadResourcePacks();
            });
        ConfigEntryBuilder entries = builder.entryBuilder();

        for (String group : GROUPS) {
            ConfigCategory category = null; // se crea solo si la pestana tiene opciones
            for (Field field : ClassicFixesConfig.class.getDeclaredFields()) {
                if (field.getType() != boolean.class || Modifier.isStatic(field.getModifiers())) continue;
                ClassicFixesConfig.Group tag = field.getAnnotation(ClassicFixesConfig.Group.class);
                if (!group.equals(tag == null ? "other" : tag.value())) continue;
                if (category == null) {
                    category = builder.getOrCreateCategory(Component.translatable("classic_fixes.config.group." + group));
                }
                category.addEntry(toggle(entries, field, config, defaults));
            }
        }
        return builder.build();
    }

    private static me.shedaniel.clothconfig2.api.AbstractConfigListEntry<?> toggle(
            ConfigEntryBuilder entries, Field field, ClassicFixesConfig config, ClassicFixesConfig defaults) {
        String key = "classic_fixes.config." + field.getName();
        try {
            var toggle = entries.startBooleanToggle(Component.translatable(key), field.getBoolean(config))
                .setDefaultValue(field.getBoolean(defaults))
                .setTooltip(Component.translatable(key + ".tooltip"))
                .setSaveConsumer(value -> {
                    try {
                        field.setBoolean(config, value);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                });
            if (field.getName().equals("betaLighting")) {
                toggle.setYesNoTextSupplier(gray -> Component.translatable(
                    gray ? "classic_fixes.lightmap.gray" : "classic_fixes.lightmap.colorized"));
            }
            return toggle.build();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
