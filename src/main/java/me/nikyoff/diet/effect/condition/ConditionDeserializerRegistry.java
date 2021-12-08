package me.nikyoff.diet.effect.condition;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializer;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ConditionDeserializerRegistry {
    private static final Map<Identifier, JsonDeserializer<? extends IDietCondition>> registry = Maps.newHashMap();

    public static void register(Identifier identifier, JsonDeserializer<? extends IDietCondition> jsonDeserializer) {
        ConditionDeserializerRegistry.registry.put(identifier, jsonDeserializer);
    }

    public static boolean contains(Identifier identifier) {
        return ConditionDeserializerRegistry.registry.containsKey(identifier);
    }

    public static JsonDeserializer<? extends IDietCondition> get(Identifier identifier) {
        return ConditionDeserializerRegistry.registry.get(identifier);
    }
}
