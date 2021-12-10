package me.nikyoff.diet.config;

import com.google.common.io.Files;
import me.nikyoff.diet.DietMod;

import java.io.BufferedReader;
import java.io.File;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public abstract class Config {
    protected final String name;

    protected final File file;

    protected boolean isLocalConfig = false;

    public Config(String modId, String name) {
        this.name = name;
        this.file = new File(String.format("config/%s/%s.json", modId, name));
    }

    public Config(String name) {
        this(DietMod.MOD_ID, name);
    }

    public String getName() {
        return name;
    }

    public void writeConfig(boolean isDefault) {
        if (isLocalConfig) {
            return;
        }

        DietMod.LOGGER.info("Writing {} config", this.name);

        try (Writer writer = Files.newWriter(this.file, StandardCharsets.UTF_8)) {
            writer.write(this.toJson(isDefault));
            writer.close();

            DietMod.LOGGER.info("Config {} recorded", this.name);
        } catch (Exception exception) {
            DietMod.LOGGER.error("Error when write {} config: {}", this.name, exception.getMessage());
        }
    }

    public void readConfig() {
        DietMod.LOGGER.info("Reading {} config", this.name);

        try(BufferedReader reader = Files.newReader(this.file, StandardCharsets.UTF_8)) {
            String json = reader.lines().collect(Collectors.joining());
            reader.close();

            Config config = this.fromJson(json);

            this.setInstance(config, false);
            DietMod.LOGGER.info("Config {} read", this.name);
        } catch (Exception exception) {
            DietMod.LOGGER.error("Error when read {} config: {}", this.name, exception.getMessage());
        }
    }

    public void initializeDefault() {
        DietMod.LOGGER.info(String.format("Initialize default %s config", this.name));

        try {
            this.file.createNewFile();
        } catch (Exception exception) {
            DietMod.LOGGER.error(String.format("Error when initialize default %s config: %s", this.name, exception.getMessage()));
            return;
        }

        this.writeConfig(true);

        DietMod.LOGGER.info(String.format("Default %s config initialized", this.name));
    }

    public void initialize() {
        DietMod.LOGGER.info("Initialize {}} config", this.name);

        try {
            if (!this.file.getParentFile().exists()) {
                this.file.getParentFile().mkdirs();
            }

            if (!this.file.exists()) {
                this.initializeDefault();
            }

            this.readConfig();

            DietMod.LOGGER.info("Config {} initialized!", this.name);
        } catch (Exception exception) {
            DietMod.LOGGER.error("Config {} initialized error: {}", this.name, exception.getMessage());
        }
    }

    public void setInstance(Config config, boolean localConfig) {
        this.isLocalConfig = localConfig;

        if (localConfig) {
            this.writeConfig(false);
        }
    }

    public abstract String toJson(boolean isDefault);

    public abstract Config fromJson(String json);

    public abstract Config getDefault();
}
