package me.nikyoff.diet.api.effect;

import java.util.Set;

public interface IDietEffectCondition {
    Set<String> getGroups();
    Double  getAbove();
    Double  getBelow();
}
