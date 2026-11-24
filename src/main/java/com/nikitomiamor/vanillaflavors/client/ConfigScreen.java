package com.nikitomiamor.vanillaflavors.client;

import com.nikitomiamor.vanillaflavors.ClassicSky;
import com.nikitomiamor.vanillaflavors.SkyPreset;
import com.nikitomiamor.vanillaflavors.VanillaFlavorsConfig;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * One toggle per boolean field of VanillaFlavorsConfig, split into tabs by their @Group.
 * Tabs follow the order of GROUPS; inside each one, fields keep their declaration order.
 */
public final class ConfigScreen {
    private static final String[] GROUPS = {"graphics", "sky", "animations", "entities", "gameplay", "other"};

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
        Map<String, BooleanListEntry> skyToggles = new HashMap<>();

        for (String group : GROUPS) {
            ConfigCategory category = null; // only created if the tab has options
            if (group.equals("sky")) {
                category = builder.getOrCreateCategory(Component.translatable("vanilla_flavors.config.group.sky"));
                EnumListEntry<ClassicSky.ColorStyle> skyColor = entries.startEnumSelector(
                        Component.translatable("vanilla_flavors.config.skyColorStyle"),
                        ClassicSky.ColorStyle.class, config.skyColorStyle)
                    .setDefaultValue(defaults.skyColorStyle)
                    .setEnumNameProvider(style -> Component.translatable("vanilla_flavors.sky." + style.name().toLowerCase()))
                    .setTooltip(Component.translatable("vanilla_flavors.config.skyColorStyle.tooltip"))
                    .setSaveConsumer(value -> config.skyColorStyle = value)
                    .build();
                category.addEntry(new PresetEntry(skyColor, skyToggles, config));
                category.addEntry(skyColor);
            }
            for (Field field : VanillaFlavorsConfig.class.getDeclaredFields()) {
                if (field.getType() != boolean.class || Modifier.isStatic(field.getModifiers())) continue;
                VanillaFlavorsConfig.Group tag = field.getAnnotation(VanillaFlavorsConfig.Group.class);
                if (!group.equals(tag == null ? "other" : tag.value())) continue;
                if (category == null) {
                    category = builder.getOrCreateCategory(Component.translatable("vanilla_flavors.config.group." + group));
                }
                var entry = toggle(entries, field, config, defaults);
                category.addEntry(entry);
                if (group.equals("sky")) skyToggles.put(field.getName(), (BooleanListEntry) entry);
                if (field.getName().equals("voidParticles")) {
                    category.addEntry(entries.startIntSlider(Component.translatable("vanilla_flavors.config.voidParticlesMaxY"),
                            Math.clamp(config.voidParticlesMaxY, -58, 0), -58, 0)
                        .setDefaultValue(defaults.voidParticlesMaxY)
                        .setTextGetter(value -> Component.literal("Y = " + value))
                        .setTooltip(Component.translatable("vanilla_flavors.config.voidParticlesMaxY.tooltip"))
                        .setSaveConsumer(value -> config.voidParticlesMaxY = value)
                        .build());
                }
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

    /**
     * Picking a version fills in the sky options on screen. Checked every frame, so editing any option by hand
     * moves this to Custom (or to the version those values match). Custom itself is skipped when cycling.
     */
    private static final class PresetEntry extends SelectionListEntry<SkyPreset> {
        private final EnumListEntry<ClassicSky.ColorStyle> color;
        private final Map<String, BooleanListEntry> toggles;
        private SkyPreset last;

        PresetEntry(EnumListEntry<ClassicSky.ColorStyle> color, Map<String, BooleanListEntry> toggles, VanillaFlavorsConfig config) {
            super(Component.translatable("vanilla_flavors.config.skyPreset"), SkyPreset.values(), initial(config),
                Component.translatable("text.cloth-config.reset_value"), () -> SkyPreset.ALPHA_1_2, value -> {},
                preset -> Component.translatable("vanilla_flavors.sky_preset." + preset.name().toLowerCase()),
                () -> Optional.of(new Component[] {Component.translatable("vanilla_flavors.config.skyPreset.tooltip")}));
            this.color = color;
            this.toggles = toggles;
            this.last = getValue();
        }

        private static SkyPreset initial(VanillaFlavorsConfig config) {
            boolean[] flags = new boolean[SkyPreset.FIELDS.length];
            try {
                for (int i = 0; i < flags.length; i++) {
                    flags[i] = VanillaFlavorsConfig.class.getField(SkyPreset.FIELDS[i]).getBoolean(config);
                }
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
            return SkyPreset.match(config.skyColorStyle, flags);
        }

        @Override
        public boolean isEdited() {
            return false; // saves nothing itself, the options it changes report the edit
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor graphics, int index, int y, int x, int width, int height,
                                       int mouseX, int mouseY, boolean hovered, float delta) {
            sync();
            super.extractRenderState(graphics, index, y, x, width, height, mouseX, mouseY, hovered, delta);
        }

        private void sync() {
            boolean[] flags = new boolean[SkyPreset.FIELDS.length];
            for (int i = 0; i < flags.length; i++) flags[i] = toggles.get(SkyPreset.FIELDS[i]).getValue();
            SkyPreset actual = SkyPreset.match(color.getValue(), flags);
            SkyPreset picked = getValue();
            if (picked != last) {
                if (picked == SkyPreset.CUSTOM && actual != SkyPreset.CUSTOM) {
                    SkyPreset[] all = SkyPreset.values();
                    picked = last == all[1] ? all[all.length - 1] : all[1];
                }
                if (picked != SkyPreset.CUSTOM) apply(picked);
            } else if (picked.color != color.getValue() || !java.util.Arrays.equals(picked.flags, flags)) {
                picked = actual; // two versions can share the same values, so keep the one that was picked
            }
            if (picked != getValue()) setInt(SelectionListEntry.class, "index", this, picked.ordinal());
            last = picked;
        }

        private void apply(SkyPreset preset) {
            setInt(SelectionListEntry.class, "index", color, preset.color.ordinal());
            for (int i = 0; i < preset.flags.length; i++) {
                try {
                    Field bool = BooleanListEntry.class.getDeclaredField("bool");
                    bool.setAccessible(true);
                    ((AtomicBoolean) bool.get(toggles.get(SkyPreset.FIELDS[i]))).set(preset.flags[i]);
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        private static void setInt(Class<?> owner, String name, Object target, int value) {
            try {
                Field field = owner.getDeclaredField(name);
                field.setAccessible(true);
                ((AtomicInteger) field.get(target)).set(value);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
