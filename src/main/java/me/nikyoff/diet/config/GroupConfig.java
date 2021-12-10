package me.nikyoff.diet.config;

import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import me.nikyoff.diet.DietMod;
import me.nikyoff.diet.config.data.GroupConfigData;
import me.nikyoff.diet.group.DietGroups;

import java.util.Set;

public class GroupConfig extends Config {
    private static GroupConfig instance = new GroupConfig();
    private static final GroupConfig defaultInstance = new GroupConfig(Set.of(
            new GroupConfigData("fruits", "#cc0000", "minecraft:apple", 1, 0.5f, 1.0f, 1.0f),
            new GroupConfigData("vegetables", "#00cc30", "minecraft:carrot", 2, 0.5f, 1.0f, 1.0f),
            new GroupConfigData("proteins", "#cc6600", "minecraft:cooked_beef", 3, 0.5f, 1.0f, 1.0f),
            new GroupConfigData("grains", "#cccc00", "minecraft:bread", 4, 0.5f, 1.0f, 1.0f),
            new GroupConfigData("sugars", "#b7ede4", "minecraft:sugar", 5, 0.5f, 1.0f, 1.0f)
    ));

    private final Set<GroupConfigData> groups = Sets.newHashSet();

    public GroupConfig() {
        super("group");
    }

    public GroupConfig(Set<GroupConfigData> groups) {
        this();

        this.groups.clear();
        this.groups.addAll(groups);
    }

    public static GroupConfig getInstance() {
        return instance;
    }

    public Set<GroupConfigData> getGroups() {
        return groups;
    }

    @Override
    public GroupConfig getDefault() {
        return GroupConfig.defaultInstance;
    }

    @Override
    public void setInstance(Config config, boolean localConfig) {
        super.setInstance(config, localConfig);

        if (config instanceof GroupConfig) {
            GroupConfig.instance = (GroupConfig) config;

            DietGroups.build(GroupConfig.instance.groups.stream().toList());
        }
    }

    @Override
    public String toJson(boolean isDefault) {
        return DietMod.GSON.toJson(isDefault ? this.getDefault().groups : this.groups);
    }

    @Override
    public GroupConfig fromJson(String json) {
        Set<GroupConfigData> groups = DietMod.GSON.fromJson(json, new TypeToken<Set<GroupConfigData>>(){}.getType());
        return new GroupConfig(groups);
    }
}
