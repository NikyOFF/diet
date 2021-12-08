package me.nikyoff.diet.impl;

import me.nikyoff.diet.api.DietModApi;
import me.nikyoff.diet.api.IDietGroup;
import me.nikyoff.diet.api.IDietResult;
import me.nikyoff.diet.api.IPlayerDietComponent;
import me.nikyoff.diet.component.DietModComponents;
import me.nikyoff.diet.group.DietGroups;
import me.nikyoff.diet.util.DietResult;
import me.nikyoff.diet.util.DietValueGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.*;

public class DietModApiImpl extends DietModApi {

    private static final DietModApi INSTANCE = new DietModApiImpl();

    public static DietModApi getInstance() {
        return DietModApiImpl.INSTANCE;
    }

    @Override
    public Set<IDietGroup> getGroups(PlayerEntity player, ItemStack input) {
        Set<IDietGroup> groups = new HashSet<>();
        Set<ItemStack> processed = new HashSet<>();
        List<ItemStack> stacks = new ArrayList<>();
        Queue<ItemStack> queue = new ArrayDeque<>();
        queue.add(input);

        while (!queue.isEmpty()) {
            ItemStack next = queue.poll();
            stacks.add(next);
        }

        for (ItemStack stack : stacks) {
            Set<IDietGroup> found = new HashSet<>();

            for (IDietGroup group : DietGroups.get()) {
                if (group.contains(stack)) {
                    found.add(group);
                }
            }

            if (found.isEmpty()) {
                groups.addAll(DietValueGenerator.get(stack.getItem()).orElse(new HashSet<>()));
            } else {
                groups.addAll(found);
            }
        }

        return groups.isEmpty() ? DietValueGenerator.get(input.getItem()).orElse(new HashSet<>()) : groups;
    }

    @Override
    public IDietResult get(PlayerEntity player, ItemStack input) {
        Set<IDietGroup> groups = getGroups(player, input);

        if (groups.isEmpty()) {
            return DietResult.EMPTY;
        }

        float healing;
        float saturation;
        Item item = input.getItem();

        if (item.isFood()) {
            healing = 1;
            saturation = item.getFoodComponent().getHunger();
        } else {
            Map<IDietGroup, Float> result = new HashMap<>();

            for (IDietGroup group : groups) {
                result.put(group, 0.0f);
            }

            return new DietResult(result);
        }

        return new DietResult(calculate(healing, saturation, groups));
    }

    @Override
    public IDietResult get(PlayerEntity player, ItemStack input, int healing, float saturation) {
        Set<IDietGroup> groups = DietModApi.getInstance().getGroups(player, input);

        if (groups.isEmpty()) {
            return DietResult.EMPTY;
        }

        return new DietResult(calculate(healing, saturation, groups));
    }

    @Override
    public Map<IDietGroup, Float> calculate(float healing, float saturation, Set<IDietGroup> groups) {
        Map<IDietGroup, Float> result = new HashMap<>();

        float quality = (healing + (healing * saturation)) / groups.size();
        float gain = (quality * 0.25f) / (quality + 15.0f);
        gain *= Math.pow(1.0f, groups.size() - 1); //1.0f - DietServerConfig.gainPenaltyPerGroup

        for (IDietGroup group : groups) {
            float value = (float) (gain * group.getGainMultiplier());
            value = Math.max(0.005f, Math.round(value * 200) / 200.0f);
            result.put(group, value);
        }

        return result;
    }

    @Override
    public IPlayerDietComponent getPlayerDietComponent(PlayerEntity player) {
        return DietModComponents.PLAYER_DIET_COMPONENT.get(player);
    }
}
