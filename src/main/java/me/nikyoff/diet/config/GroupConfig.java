package me.nikyoff.diet.config;

import com.google.common.collect.Sets;
import com.google.common.io.Files;
import me.nikyoff.diet.DietMod;
import me.nikyoff.diet.config.data.GroupConfigData;
import me.nikyoff.diet.group.DietGroups;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class GroupConfig {
    public static final String NAME = "group";
    @Nullable public static GroupConfig INSTANCE = null;

    private static final File file = new File(String.format("config/%s/%s.json", DietMod.MOD_ID, GroupConfig.NAME));

    private final Set<GroupConfigData> groups = Sets.newHashSet();

    public GroupConfig() {
        groups.add(new GroupConfigData("fruits", "#f04a4a", "minecraft:apple", 1, 0.1f, 1.0f, 1.0f));
        groups.add(new GroupConfigData("vegetables", "#7cf28b", "minecraft:carrot", 2, 0.1f, 1.0f, 1.0f));
        groups.add(new GroupConfigData("proteins", "#cf7f42", "minecraft:cooked_beef", 3, 0.1f, 1.0f, 1.0f));
        groups.add(new GroupConfigData("grains", "#fce668", "minecraft:bread", 4, 0.1f, 1.0f, 1.0f));
        groups.add(new GroupConfigData("sugars", "#ab5de3", "minecraft:honey_bottle", 5, 0.1f, 1.0f, 1.0f));
    }

    public static String toJSON(GroupConfig config) {
        return DietMod.GSON.toJson(GroupConfig.class);
    }

    public static GroupConfig fromJSON(String json) {
        return DietMod.GSON.fromJson(json, GroupConfig.class);
    }

    public static void writeConfig(GroupConfig config) {
        DietMod.LOGGER.info(String.format("Writing %s config", GroupConfig.NAME));

        try (Writer writer = Files.newWriter(GroupConfig.file, StandardCharsets.UTF_8)) {
            DietMod.GSON.toJson(config, writer);
            DietMod.LOGGER.info(String.format("Config %s recorded", GroupConfig.NAME));
        } catch (Exception exception) {
            DietMod.LOGGER.error(String.format("Error when write %s config: %s", GroupConfig.NAME, exception.getMessage()));
        }
    }

    public static void readConfig() {
        DietMod.LOGGER.info(String.format("Reading %s config", GroupConfig.NAME));

        try (BufferedReader reader = Files.newReader(GroupConfig.file, StandardCharsets.UTF_8)) {
            GroupConfig.INSTANCE = DietMod.GSON.fromJson(reader, GroupConfig.class);

            DietGroups.build(GroupConfig.INSTANCE.groups.stream().toList());

            DietMod.LOGGER.info(String.format("Config %s readed", GroupConfig.NAME));
        } catch (Exception exception) {
            DietMod.LOGGER.error(String.format("Error when read %s config: %s", GroupConfig.NAME, exception.getMessage()));
        }
    }

    public static void initializeDefault() {
        DietMod.LOGGER.info(String.format("Initialize default %s config", GroupConfig.NAME));

        try {
            GroupConfig.file.createNewFile();
        } catch (Exception exception) {
            DietMod.LOGGER.error(String.format("Error when initialize default %s config: %s", GroupConfig.NAME, exception.getMessage()));
            return;
        }

        GroupConfig.writeConfig(new GroupConfig());

        DietMod.LOGGER.info(String.format("Default %s config initialized", GroupConfig.NAME));
    }

    public static void initialize() {
        DietMod.LOGGER.info(String.format("Initialize %s config", GroupConfig.NAME));

        try {
            if (!GroupConfig.file.getParentFile().exists()) {
                GroupConfig.file.getParentFile().mkdirs();
            }

            if (!GroupConfig.file.exists()) {
                GroupConfig.initializeDefault();
            }

            GroupConfig.readConfig();

            DietMod.LOGGER.info(String.format("Config %s initialized!", GroupConfig.NAME));
        } catch (Exception exception) {
            DietMod.LOGGER.error(String.format("Config %s initialized error", GroupConfig.NAME), exception);
        }
    }
}
