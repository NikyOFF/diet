package me.nikyoff.diet.config;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.annotations.SerializedName;
import me.nikyoff.diet.DietMod;

import java.util.Map;

public class OverriddenFoodConfig extends Config {

    private static OverriddenFoodConfig instance = new OverriddenFoodConfig();
    private static final OverriddenFoodConfig defaultInstance = new OverriddenFoodConfig(Map.of("minecraft:enchanted_golden_apple", Map.of("fruits", 0.2f)));

    @SerializedName("overridden_food")
    private final Map<String, Map<String, Float>> overriddenFood = Maps.newHashMap();

    public OverriddenFoodConfig() {
        super("overridden_food");
    }

    public OverriddenFoodConfig(Map<String, Map<String, Float>> overriddenFood) {
        this();

        this.overriddenFood.clear();
        this.overriddenFood.putAll(overriddenFood);
    }

    public static OverriddenFoodConfig getInstance() {
        return instance;
    }

    public Map<String, Map<String, Float>> getOverriddenFood() {
        return overriddenFood;
    }

    @Override
    public OverriddenFoodConfig getDefault() {
        return OverriddenFoodConfig.defaultInstance;
    }

    @Override
    public void setInstance(Config config, boolean localConfig) {
        super.setInstance(config, localConfig);

        if (config instanceof OverriddenFoodConfig) {
            OverriddenFoodConfig.instance = (OverriddenFoodConfig) config;
        }
    }

    @Override
    public String toJson(boolean isDefault) {
        return DietMod.GSON.toJson(isDefault ? this.getDefault().overriddenFood : this.overriddenFood);
    }

    @Override
    public OverriddenFoodConfig fromJson(String json) {
        Map<String, Map<String, Float>> overriddenFood = DietMod.GSON.fromJson(json, new TypeToken<Map<String, Map<String, Float>>>(){}.getType());
        return new OverriddenFoodConfig(overriddenFood);
    }
}
