package me.nikyoff.diet.util;

import me.nikyoff.core.component.CoreModComponents;
import me.nikyoff.core.component.PlayerFoodDataComponent;
import me.nikyoff.diet.DietMod;
import me.nikyoff.diet.api.IDietGroup;
import me.nikyoff.diet.api.IDietResult;
import me.nikyoff.diet.impl.DietModApiImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DietItemTooltip {
    public static void applyTooltip(ItemStack itemStack, List<Text> tooltip) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        if (minecraftClient.player == null) {
            return;
        }

        PlayerFoodDataComponent playerFoodDataComponent = CoreModComponents.PLAYER_FOOD_DATA_COMPONENT.get(minecraftClient.player);

        if (playerFoodDataComponent.getFoodEatenCount(itemStack.getItem()) <= 0 && !minecraftClient.player.isCreative()) {
            return;
        }

        IDietResult result = DietModApiImpl.getInstance().get(minecraftClient.player, itemStack);

        if (result == DietResult.EMPTY) {
            return;
        }

        Map<IDietGroup, Float> groups = result.get();

        if (groups.isEmpty()) {
            return;
        }

        List<Text> groupsTooltips = new ArrayList<>();
        boolean special = DietMod.SPECIAL_FOOD.contains(itemStack.getItem());

        for (Map.Entry<IDietGroup, Float> entry : groups.entrySet()) {
            IDietGroup group = entry.getKey();
            float value = entry.getValue();

            TranslatableText groupName = new TranslatableText(String.format("%s.groups.%s.name", DietMod.MOD_ID, group.getName()));

            if (special) {
                groupsTooltips.add(new TranslatableText(String.format("%s.tooltip.group.special", DietMod.MOD_ID), groupName).formatted(Formatting.GREEN));
            } else if (value > 0.0F) {
                groupsTooltips.add(new TranslatableText(String.format("%s.tooltip.group", DietMod.MOD_ID), String.format("%.2f", entry.getValue() * 100), groupName).formatted(Formatting.GREEN));
            }
        }

        if (!groupsTooltips.isEmpty()) {
            tooltip.add(LiteralText.EMPTY);
            tooltip.add(new TranslatableText(String.format("%s.tooltip.eaten", DietMod.MOD_ID)).formatted(Formatting.GRAY));
            tooltip.addAll(groupsTooltips);
        }
    }
}
