package me.nikyoff.diet.config.data;

import com.google.gson.annotations.SerializedName;
import me.nikyoff.diet.effect.common.DietAttribute;
import me.nikyoff.diet.effect.common.DietStatusEffect;
import me.nikyoff.diet.effect.condition.IDietCondition;

import java.util.ArrayList;
import java.util.List;

public class EffectConfigData {

    private List<DietAttribute> attributes = new ArrayList<>();

    @SerializedName("status_effects")
    private List<DietStatusEffect> statusEffects = new ArrayList<>();

    @SerializedName("diet_conditions")
    private List<IDietCondition> dietConditions = new ArrayList<>();

    @SerializedName("match_method")
    private String matchMethod;

    public EffectConfigData(List<DietAttribute> attributes, List<DietStatusEffect> statusEffects, List<IDietCondition> dietConditions, String matchMethod) {
        this.attributes = attributes;
        this.statusEffects = statusEffects;
        this.dietConditions = dietConditions;
        this.matchMethod = matchMethod;
    }

    public List<DietAttribute> getAttributes() {
        return attributes;
    }

    public List<DietStatusEffect> getStatusEffects() {
        return statusEffects;
    }

    public List<IDietCondition> getDietConditions() {
        return dietConditions;
    }

    public String getMatchMethod() {
        return matchMethod;
    }
}
