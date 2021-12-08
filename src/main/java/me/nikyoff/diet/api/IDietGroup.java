package me.nikyoff.diet.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;

import java.awt.*;

public interface IDietGroup {
    String getName();

    Color getColor();

    Item getIcon();

    Integer getOrder();

    Float getDefaultValue();

    Float getGainMultiplier();

    Float getDecayMultiplier();

    Tag<Item> getTag();

    boolean contains(Item item);

    boolean contains(ItemStack stack);
}
