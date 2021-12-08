package me.nikyoff.diet.config.data;

import com.google.gson.annotations.SerializedName;

public class GroupConfigData {
    public String name;
    public String color;
    public String icon;
    public Integer order;

    @SerializedName("default_value")
    public Float defaultValue;

    @SerializedName("gain_multiplier")
    public Float gainMultiplier;

    @SerializedName("decay_multiplier")
    public Float decayMultiplier;

    public GroupConfigData(String name, String color, String icon, Integer order, Float defaultValue, Float gainMultiplier, Float decayMultiplier) {
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.order = order;
        this.defaultValue = defaultValue;
        this.gainMultiplier = gainMultiplier;
        this.decayMultiplier = decayMultiplier;
    }
}
