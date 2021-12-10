package me.nikyoff.diet.effect.condition;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Map;

public class ConditionDeserializerRegistry {
    private static final Map<Identifier, Pair<JsonSerializer<? super IDietCondition>, JsonDeserializer<? extends IDietCondition>>> registry = Maps.newHashMap();

    public static void register(Identifier identifier, Pair<JsonSerializer<? super IDietCondition>, JsonDeserializer<? extends IDietCondition>> jsonSerializerDeserializer) {
        ConditionDeserializerRegistry.registry.put(identifier, jsonSerializerDeserializer);
    }

    public static boolean contains(Identifier identifier) {
        return ConditionDeserializerRegistry.registry.containsKey(identifier);
    }

    public static Pair<JsonSerializer<? super IDietCondition>, JsonDeserializer<? extends IDietCondition>> get(Identifier identifier) {
        return ConditionDeserializerRegistry.registry.get(identifier);
    }
}
