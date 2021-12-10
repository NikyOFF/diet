package me.nikyoff.diet.effect.condition;

import com.google.gson.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;
import java.util.Set;

public interface IDietCondition {
    Identifier getType();

    Set<String> getGroups();

    int getMatches(PlayerEntity playerEntity);

    int getMultiplier(int matches);

    public static class JsonSerializerDeserializer implements JsonSerializer<IDietCondition>, JsonDeserializer<IDietCondition> {

        @Override
        public JsonElement serialize(IDietCondition src, Type typeOfSrc, JsonSerializationContext context) {
            return ConditionDeserializerRegistry.get(src.getType()).getLeft().serialize(src, typeOfSrc, context);
        }

        @Override
        public IDietCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            if (!jsonObject.has("type")) {
                throw new JsonParseException("A required attribute is missing!");
            }

            return ConditionDeserializerRegistry.get(new Identifier(jsonObject.get("type").getAsString())).getRight().deserialize(json, typeOfT, context);
        }

    }
}
