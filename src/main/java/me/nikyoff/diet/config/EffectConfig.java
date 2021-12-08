package me.nikyoff.diet.config;

import com.google.common.collect.Sets;
import com.google.common.io.Files;
import me.nikyoff.diet.DietMod;
import me.nikyoff.diet.config.data.EffectConfigData;
import me.nikyoff.diet.effect.DietEffects;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class EffectConfig {
    public static final String NAME = "effects";
    @Nullable public static EffectConfig INSTANCE = null;

    private static final File file = new File(String.format("config/%s/%s.json", DietMod.MOD_ID, EffectConfig.NAME));

    private final Set<EffectConfigData> effects = Sets.newHashSet();

    public EffectConfig() {
    }

    public static void writeConfig(EffectConfig config) {
        DietMod.LOGGER.info(String.format("Writing %s config", GroupConfig.NAME));

        try (Writer writer = Files.newWriter(EffectConfig.file, StandardCharsets.UTF_8)) {
            DietMod.GSON.toJson(config, writer);

            DietMod.LOGGER.info(String.format("Config %s recorded", EffectConfig.NAME));
        } catch (Exception exception) {
            DietMod.LOGGER.error(String.format("Error when write %s config: %s", EffectConfig.NAME, exception.getMessage()));
        }
    }

    public static void readConfig() {
        DietMod.LOGGER.info("Reading Seasons Config");

        try(BufferedReader reader = Files.newReader(EffectConfig.file, StandardCharsets.UTF_8)) {
            EffectConfig.INSTANCE = DietMod.GSON.fromJson(reader, EffectConfig.class);

            DietEffects.build(EffectConfig.INSTANCE.effects.stream().toList());

            DietMod.LOGGER.info(String.format("Config %s readed", EffectConfig.NAME));
        } catch (Exception exception) {
            DietMod.LOGGER.error(String.format("Error when read %s config: %s", EffectConfig.NAME, exception.getMessage()));
        }
    }

    public static void initializeDefault() {
        DietMod.LOGGER.info(String.format("Initialize default %s config", EffectConfig.NAME));

        try {
            EffectConfig.file.createNewFile();
        } catch (Exception exception) {
            DietMod.LOGGER.error(String.format("Error when initialize default %s config: %s", EffectConfig.NAME, exception.getMessage()));
            return;
        }

        EffectConfig.writeConfig(new EffectConfig());

        DietMod.LOGGER.info(String.format("Default %s config initialized", EffectConfig.NAME));
    }

    public static void initialize() {
        DietMod.LOGGER.info(String.format("Initialize %s config", EffectConfig.NAME));

        try {
            if (!EffectConfig.file.getParentFile().exists()) {
                EffectConfig.file.getParentFile().mkdirs();
            }

            if (!EffectConfig.file.exists()) {
                EffectConfig.initializeDefault();
            }

            EffectConfig.readConfig();

            DietMod.LOGGER.info(String.format("Config %s initialized!", EffectConfig.NAME));
        } catch (Exception exception) {
            DietMod.LOGGER.error(String.format("Config %s initialized error", EffectConfig.NAME), exception);
        }
    }
}
