package me.nikyoff.diet.effect.condition;

import com.google.gson.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.Set;

public interface IDietCondition {
    Identifier getType();

    Set<String> getGroups();

    int getMatches(PlayerEntity playerEntity);

    int getMultiplier(int matches);

    public static class Deserializer implements JsonDeserializer<IDietCondition> {

        @Override
        public IDietCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            if (!jsonObject.has("type")) {
                throw new JsonParseException("A required attribute is missing!");
            }

            return ConditionDeserializerRegistry.get(new Identifier(jsonObject.get("type").getAsString())).deserialize(json, typeOfT, context);
        }

    }
}
