package com.nikitomiamor.vanillaflavors.client;

import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * One toggle per boolean field of VanillaFlavorsConfig, split into tabs by their @Group.
 * Tabs follow the order of GROUPS; inside each one, fields keep their declaration order.
 */
public final class ConfigScreen {
    private static final String[] GROUPS = {"graphics", "animations", "entities", "gameplay", "other"};

    private ConfigScreen() {}

    public static Screen create(Screen parent) {
        VanillaFlavorsConfig config = VanillaFlavorsConfig.get();
        VanillaFlavorsConfig defaults = new VanillaFlavorsConfig();
        boolean aoBefore = config.oldSmoothLighting;
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.translatable("vanilla_flavors.config.title"))
            .setSavingRunnable(() -> {
                VanillaFlavorsConfig.save();
                // smooth lighting (chunk meshes) only changes on reload; the lightmap style changes instantly
                if (config.oldSmoothLighting != aoBefore) Minecraft.getInstance().reloadResourcePacks();
            });
        ConfigEntryBuilder entries = builder.entryBuilder();

        for (String group : GROUPS) {
            ConfigCategory category = null; // only created if the tab has options
            for (Field field : VanillaFlavorsConfig.class.getDeclaredFields()) {
                if (field.getType() != boolean.class || Modifier.isStatic(field.getModifiers())) continue;
                VanillaFlavorsConfig.Group tag = field.getAnnotation(VanillaFlavorsConfig.Group.class);
                if (!group.equals(tag == null ? "other" : tag.value())) continue;
                if (category == null) {
                    category = builder.getOrCreateCategory(Component.translatable("vanilla_flavors.config.group." + group));
                }
                category.addEntry(toggle(entries, field, config, defaults));
            }
        }
        return builder.build();
    }

    private static me.shedaniel.clothconfig2.api.AbstractConfigListEntry<?> toggle(
            ConfigEntryBuilder entries, Field field, VanillaFlavorsConfig config, VanillaFlavorsConfig defaults) {
        String key = "vanilla_flavors.config." + field.getName();
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
                    gray ? "vanilla_flavors.lightmap.gray" : "vanilla_flavors.lightmap.colorized"));
            }
            return toggle.build();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
