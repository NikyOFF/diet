package me.nikyoff.diet.util;

import me.nikyoff.diet.api.IDietGroup;
import me.nikyoff.diet.api.IDietResult;

import java.util.HashMap;
import java.util.Map;

public class DietResult implements IDietResult {
    public static final IDietResult EMPTY = new DietResult();

    private final Map<IDietGroup, Float> groups;

    private DietResult() {
        this(new HashMap<>());
    }

    public DietResult(Map<IDietGroup, Float> groups) {
        this.groups = groups;
    }

    @Override
    public Map<IDietGroup, Float> get() {
        return groups;
    }

    public float get(IDietGroup group) {
        return groups.getOrDefault(group, 0.0f);
    }
}
