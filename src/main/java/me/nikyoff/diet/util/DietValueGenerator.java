package me.nikyoff.diet.util;

import com.google.common.base.Stopwatch;
import me.nikyoff.diet.DietMod;
import me.nikyoff.diet.api.IDietGroup;
import me.nikyoff.diet.group.DietGroups;
import me.nikyoff.diet.network.GeneratedValuesSyncS2CPacket;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.Registry;

import java.util.*;
import java.util.stream.Collectors;

public class DietValueGenerator {
    private static final Map<Item, Set<IDietGroup>> GENERATED = new HashMap<>();
    private static final Stopwatch STOPWATCH = Stopwatch.createUnstarted();

    public static void putAll(Map<Item, Set<IDietGroup>> generated) {
        DietValueGenerator.GENERATED.putAll(generated);
    }

    public static void reload(MinecraftServer minecraftServer) {
        DietMod.LOGGER.info("Generating diet values...");
        STOPWATCH.reset();
        STOPWATCH.start();
        DietMod.LOGGER.info("Finding ungrouped food items...");

        GENERATED.clear();

        RecipeManager recipeManager = minecraftServer.getRecipeManager();
        Set<Item> ungroupedFood = new HashSet<>();
        Set<IDietGroup> groups = DietGroups.get();

        items:
        for (Item item : minecraftServer.getRegistryManager().get(Registry.ITEM_KEY).stream().toList()) {

            if (item.isFood() || DietMod.SPECIAL_FOOD.contains(item)) {

                for (IDietGroup dietGroup : groups) {

                    if (dietGroup.contains(item)) {
                        continue items;
                    }
                }

                ungroupedFood.add(item);
            }
        }

        DietMod.LOGGER.info("Found {} ungrouped food items", ungroupedFood.size());
        DietMod.LOGGER.info("Finding recipes...");

        Map<Item, Recipe<?>> recipes = new HashMap<>();
        List <Recipe<?>> sortedRecipes = recipeManager.values().stream().sorted(Comparator.comparing(Recipe::getId)).collect(Collectors.toList());
        Set<Recipe<?>> processedRecipes = new HashSet<>();

        for (Recipe<?> recipe : sortedRecipes) {
            ItemStack output = recipe.getOutput();
            Item item = output.getItem();

            if (ungroupedFood.contains(item) && !processedRecipes.contains(recipe)) {
                recipes.putIfAbsent(item, recipe);
                traverseRecipes(processedRecipes, recipes, sortedRecipes, recipe);
            }
        }

        DietMod.LOGGER.info("Found {} recipes to process", recipes.size());
        DietMod.LOGGER.info("Processing items...");

        Set<Item> processedItems = new HashSet<>();

        for (Map.Entry<Item, Recipe<?>> entry : recipes.entrySet()) {
            Item item = entry.getKey();

            if (!processedItems.contains(item)) {
                traverseIngredients(processedItems, recipes, groups, item);
            }
        }

        DietMod.LOGGER.info("Processed {} items", processedItems.size());
        STOPWATCH.stop();
        DietMod.LOGGER.info("Generating diet values took {}", STOPWATCH);

        GeneratedValuesSyncS2CPacket.send(minecraftServer, DietValueGenerator.GENERATED);
    }

    private static void traverseRecipes(Set<Recipe<?>> processed, Map<Item, Recipe<?>> recipes, List<Recipe<?>> allRecipes, Recipe<?> recipe) {
        processed.add(recipe);

        for (Ingredient ingredient : recipe.getIngredients()) {
            Arrays.stream(ingredient.getMatchingStacks())
                    .min(Comparator.comparing(ItemStack::getTranslationKey))
                    .ifPresent(stack -> {
                        for (Recipe<?> entry : allRecipes) {
                            ItemStack output = entry.getOutput();
                            Item item = output.getItem();

                            if (item == stack.getItem() && !processed.contains(entry)) {
                                recipes.putIfAbsent(item, entry);
                                traverseRecipes(processed, recipes, allRecipes, entry);
                            }
                        }
                    });
        }
    }

    private static Set<IDietGroup> traverseIngredients(Set<Item> processed, Map<Item, Recipe<?>> recipes, Set<IDietGroup> groups, Item item) {
        processed.add(item);

        Set<IDietGroup> result = new HashSet<>();
        ItemStack fillerStack = new ItemStack(item);

        for (IDietGroup group : groups) {

            if (group.contains(fillerStack)) {
                result.add(group);
            }
        }

        if (result.isEmpty()) {
            Recipe<?> recipe = recipes.get(item);

            if (recipe != null) {

                for (Ingredient ingredient : recipe.getIngredients()) {
                    Arrays.stream(ingredient.getMatchingStacks())
                            .min(Comparator.comparing(ItemStack::getTranslationKey))
                            .ifPresent(stack -> {
                                Item matchingItem = stack.getItem();

                                if (!DietMod.INGREDIENTS.contains(matchingItem)) {
                                    Set<IDietGroup> fallback = GENERATED.get(matchingItem);

                                    if (fallback != null) {
                                        result.addAll(fallback);
                                    } else if (!processed.contains(matchingItem)) {
                                        Set<IDietGroup> found = new HashSet<>();

                                        for (IDietGroup group : groups) {

                                            if (group.contains(stack)) {
                                                found.add(group);
                                            }
                                        }

                                        if (found.isEmpty()) {
                                            found.addAll(traverseIngredients(processed, recipes, groups, matchingItem));
                                        }
                                        GENERATED.putIfAbsent(matchingItem, found);
                                        result.addAll(found);
                                    }
                                }
                            });
                }
            }
        }

        GENERATED.putIfAbsent(item, result);

        return result;
    }

    public static Optional<Set<IDietGroup>> get(Item item) {
        return Optional.ofNullable(GENERATED.get(item));
    }
}
