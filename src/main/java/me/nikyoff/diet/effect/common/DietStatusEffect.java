package me.nikyoff.diet.effect.common;

import com.google.gson.*;
import me.nikyoff.diet.DietMod;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;

public class DietStatusEffect {
    private StatusEffect statusEffect;
    private int power;

    public DietStatusEffect(StatusEffect statusEffect, int power) {
        this.statusEffect = statusEffect;
        this.power = power;
    }

    public StatusEffect getStatusEffect() {
        return statusEffect;
    }

    public int getPower() {
        return power;
    }

    public static class JsonFormat {
        public String name;
        public Integer power;

        public JsonFormat(String name, Integer power) {
            this.name = name;
            this.power = power;
        }

    }

    public static class JsonSerializerDeserializer implements JsonSerializer<DietStatusEffect>, JsonDeserializer<DietStatusEffect> {

        @Override
        public JsonElement serialize(DietStatusEffect src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("name", Registry.STATUS_EFFECT.getId(src.statusEffect).toString());
            jsonObject.addProperty("power", src.power);

            return jsonObject;
        }

        @Override
        public DietStatusEffect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonFormat jsonFormat = DietMod.GSON.fromJson(json, JsonFormat.class);

            if (jsonFormat.name == null || jsonFormat.power == null) {
                throw new JsonSyntaxException("(DietStatusEffect deserialize) A required attribute is missing!");
            }

            Identifier statusEffectIdentifier = new Identifier(jsonFormat.name);

            if (!Registry.STATUS_EFFECT.containsId(statusEffectIdentifier)) {
                throw new JsonParseException(String.format("Cannot find %s status effect in registry", jsonFormat.name));
            }

            return new DietStatusEffect(
                    Registry.STATUS_EFFECT.get(statusEffectIdentifier),
                    jsonFormat.power
            );
        }

    }

}
