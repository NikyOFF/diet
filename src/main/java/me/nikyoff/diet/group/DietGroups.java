package me.nikyoff.diet.group;

import com.google.common.collect.ImmutableSet;
import me.nikyoff.diet.DietMod;
import me.nikyoff.diet.api.IDietGroup;
import me.nikyoff.diet.config.data.GroupConfigData;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DietGroups {
    private static final Item replacedIcon = Items.BARRIER;
    private static final Set<IDietGroup> groups = new TreeSet<>(Comparator.comparingInt(IDietGroup::getOrder).thenComparing(IDietGroup::getName));

    public static Set<IDietGroup> get() {
        return ImmutableSet.copyOf(DietGroups.groups);
    }

    @SuppressWarnings("all")
    public static void build(final List<GroupConfigData> configs) {
        DietGroups.groups.clear();

        DietMod.LOGGER.info("clear groups");

        if (configs.isEmpty()) {
            DietMod.LOGGER.info("configs is empty");
            return;
        }

        int configsSize = configs.size();

        for (GroupConfigData groupConfigData : configs) {
            String name = groupConfigData.name;
            Color color = Color.decode(groupConfigData.color);
            Item icon = Registry.ITEM.get(new Identifier(groupConfigData.icon));
            int order = MathHelper.clamp(groupConfigData.order, 0, configsSize);
            Float defaultValue = MathHelper.clamp(groupConfigData.defaultValue, 0, 1);
            Float gainMultiplier = MathHelper.clamp(groupConfigData.gainMultiplier, 0, 1);
            Float decayMultiplier = MathHelper.clamp(groupConfigData.decayMultiplier, 0, 1);

            if (icon.asItem() == null) {
                DietMod.LOGGER.warn("Found unknown item in diet groups config: {}", name);
                icon = DietGroups.replacedIcon;
                DietMod.LOGGER.info("Unknown item replaced to: {}", DietGroups.replacedIcon.getName());
            }

            if (color == null) {
                DietMod.LOGGER.warn("Found unknown color in diet groups config: {}", name);
                color = Color.gray;
                DietMod.LOGGER.info("Unknown color replaced to gray", DietGroups.replacedIcon.getName());
            }

            IDietGroup group = new DietGroup(name, color, icon, order, defaultValue, gainMultiplier, decayMultiplier);

            if (!DietGroups.groups.add(group)) {
                DietMod.LOGGER.error("Found duplicate ID in diet groups config: {}", name);
            }
        }
    }
}
