package me.nikyoff.diet.api;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class DietModApi {

  public static DietModApi getInstance() {
    throw new IllegalArgumentException("Missing API implementation for Diet!");
  }

  public Set<IDietGroup> getGroups(PlayerEntity player, ItemStack stack) {
    return new HashSet<>();
  }

  public IDietResult get(PlayerEntity player, ItemStack stack) {
    return HashMap::new;
  }

  public IDietResult get(PlayerEntity player, ItemStack stack, int food, float saturation) {
    return HashMap::new;
  }

  public Map<IDietGroup, Float> calculate(float healing, float saturation, Set<IDietGroup> groups) {
    return Maps.newHashMap();
  }

  public IPlayerDietComponent getPlayerDietComponent(PlayerEntity player) {
    return null;
  }
}
