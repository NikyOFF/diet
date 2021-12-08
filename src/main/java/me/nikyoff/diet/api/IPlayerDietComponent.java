package me.nikyoff.diet.api;

import dev.onyxstudios.cca.api.v3.component.Component;
import me.nikyoff.diet.effect.DietEffect;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.Set;

public interface IPlayerDietComponent extends Component {
    Set<String> getAvailableGroups();

    Boolean hasAvailableGroup(String name);

    Boolean addAvailableGroup(String group);

    Boolean removeAvailableGroup(String group);

    void setAvailableGroups(Set<String> availableGroups);

    Map<String, Float> getGroupValues();

    void setGroupValues(Map<String, Float> groupValues);

    void setGroupValue(String groupName, float groupValue);

    void onConsume(ItemStack itemStack);

    void applyDecay(int value);

    void applyDietEffect(DietEffect dietEffect, int multiplier);

    void cancelDietEffect(DietEffect dietEffect);

    void applyEffects();

    void cancelEffects();

    void updateEffects();
}
