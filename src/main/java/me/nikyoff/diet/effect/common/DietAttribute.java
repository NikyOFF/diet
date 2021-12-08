package me.nikyoff.diet.effect.common;

import com.google.gson.*;
import me.nikyoff.diet.DietMod;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;

public class DietAttribute {
    public EntityAttribute attribute;
    public EntityAttributeModifier.Operation operation;
    public double amount;

    public DietAttribute(EntityAttribute attribute, EntityAttributeModifier.Operation operation, double amount) {
        this.attribute = attribute;
        this.operation = operation;
        this.amount = amount;
    }

    public static class JsonFormat {
        public String name;
        public String operation;
        public Double amount;

        public JsonFormat(String name, String operation, Double amount) {
            this.name = name;
            this.operation = operation;
            this.amount = amount;
        }

    }

    public static class Serializer implements JsonSerializer<DietAttribute> {

        @Override
        public JsonElement serialize(DietAttribute src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("attribute", Registry.ATTRIBUTE.getId(src.attribute).toString());
            jsonObject.addProperty("operation", src.operation.toString());
            jsonObject.addProperty("amount", src.amount);

            return jsonObject;
        }

    }

    public static class Deserializer implements JsonDeserializer<DietAttribute> {

        @Override
        public DietAttribute deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonFormat jsonFormat = DietMod.GSON.fromJson(json, JsonFormat.class);

            if (jsonFormat.name == null || jsonFormat.operation == null || jsonFormat.amount == null) {
                throw new JsonSyntaxException("A required attribute is missing!");
            }

            Identifier attributeIdentifier = new Identifier(jsonFormat.name);

            if (!Registry.ATTRIBUTE.containsId(attributeIdentifier)) {
                throw new JsonParseException(String.format("Cannot find %s attribute in registry", jsonFormat.name));
            }

            return new DietAttribute(
                    Registry.ATTRIBUTE.get(attributeIdentifier),
                    EntityAttributeModifier.Operation.valueOf(jsonFormat.operation.toUpperCase()),
                    jsonFormat.amount
            );
        }

    }

}
