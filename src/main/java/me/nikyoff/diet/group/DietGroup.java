package me.nikyoff.diet.group;

import me.nikyoff.diet.DietMod;
import me.nikyoff.diet.api.IDietGroup;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.awt.*;

public class DietGroup implements IDietGroup {
    public final String name;
    public final Color color;
    public final Item icon;
    public final Integer order;
    public final Float defaultValue;
    public final Float gainMultiplier;
    public final Float decayMultiplier;
    public final Tag<Item> tag;

    public DietGroup(String name, Color color, Item icon,  Integer order, Float defaultValue, Float gainMultiplier, Float decayMultiplier) {
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.order = order;
        this.defaultValue = defaultValue;
        this.gainMultiplier = gainMultiplier;
        this.decayMultiplier = decayMultiplier;

        Tag<Item> candidate = ItemTags.getTagGroup().getTag(new Identifier(DietMod.MOD_ID, name));

        if (candidate == null) {
            this.tag = TagFactory.ITEM.create(new Identifier(DietMod.MOD_ID, name));
        } else {
            this.tag = candidate;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public Item getIcon() {
        return this.icon;
    }

    @Override
    public Integer getOrder() {
        return this.order;
    }


    @Override
    public Float getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public Float getGainMultiplier() {
        return this.gainMultiplier;
    }

    @Override
    public Float getDecayMultiplier() {
        return this.decayMultiplier;
    }

    @Override
    public Tag<Item> getTag() {
        return this.tag;
    }

    @Override
    public boolean contains(Item item) {
        return this.tag.contains(item);
    }

    @Override
    public boolean contains(ItemStack stack) {
        return this.contains(stack.getItem());
    }
}
