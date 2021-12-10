package me.nikyoff.diet.effect.common;

import com.google.gson.*;
import me.nikyoff.diet.DietMod;
import me.nikyoff.diet.component.DietModComponents;
import me.nikyoff.diet.component.PlayerDietComponent;
import me.nikyoff.diet.effect.condition.IDietCondition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DietCondition implements IDietCondition {
    public static final Identifier TYPE = new Identifier(DietMod.MOD_ID, "range");

    private final Set<String> groups;
    private final MatchMethod matchMethod;
    private final float above;
    private final float below;

    public DietCondition(Set<String> groups, MatchMethod matchMethod, float above, float below) {
        this.groups = groups;
        this.matchMethod = matchMethod;
        this.above = above;
        this.below = below;
    }

    @Override
    public Identifier getType() {
        return DietCondition.TYPE;
    }

    @Override
    public Set<String> getGroups() {
        return groups;
    }

    @Override
    public int getMatches(PlayerEntity playerEntity) {
        PlayerDietComponent playerDietComponent = DietModComponents.PLAYER_DIET_COMPONENT.get(playerEntity);

        Map<String, Float> groupValues = playerDietComponent.getGroupValues();

        return this.matchMethod.getMatches(this.groups, groupValues, this.above, this.below);
    }

    @Override
    public int getMultiplier(int matches) {
        if (this.matchMethod == MatchMethod.EVERY) {
            return matches;
        }

        return 1;
    }

    public MatchMethod getMatchMethod() {
        return matchMethod;
    }

    public float getAbove() {
        return above;
    }

    public float getBelow() {
        return below;
    }

    public enum MatchMethod {
        EVERY {
            @Override
            int getMatches(Set<String> groups, Map<String, Float> values, float above, float below) {
                int count = 0;

                for (String group : groups) {
                    Float value = values.get(group);

                    if (value != null && MatchMethod.inRange(value, above, below)) {
                        count++;
                    }
                }

                return count;
            }
        },
        ANY {
            @Override
            int getMatches(Set<String> groups, Map<String, Float> values, float above, float below) {

                for (String group : groups) {
                    Float value = values.get(group);

                    if (value != null && MatchMethod.inRange(value, above, below)) {
                        return 1;
                    }
                }

                return 0;
            }
        },
        AVERAGE {
            @Override
            int getMatches(Set<String> groups, Map<String, Float> values, float above, float below) {
                float sum = 0;

                for (String group : groups) {
                    Float value = values.get(group);

                    if (value != null) {
                        sum += value;
                    }
                }

                return MatchMethod.inRange(sum / (float) groups.size(), above, below) ? 1 : 0;
            }
        },
        ALL {
            @Override
            int getMatches(Set<String> groups, Map<String, Float> values, float above, float below) {

                for (String group : groups) {
                    Float value = values.get(group);

                    if (value == null || !MatchMethod.inRange(value, above, below)) {
                        return 0;
                    }
                }

                return 1;
            }
        },
        NONE {
            @Override
            int getMatches(Set<String> groups, Map<String, Float> values, float above, float below) {

                for (String group : groups) {
                    Float value = values.get(group);

                    if (value != null && MatchMethod.inRange(value, above, below)) {
                        return 0;
                    }
                }

                return 1;
            }
        };

        abstract int getMatches(Set<String> groups, Map<String, Float> values, float above, float below);

        public static MatchMethod findOrDefault(String val, MatchMethod def) {
            try {
                return MatchMethod.valueOf(val.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                DietMod.LOGGER.error("No such match method {}", val);
            }

            return def;
        }

        private static boolean inRange(float value, float above, float below) {
            return value >= above && value <= below;
        }
    }

    public static class JsonFormat {
        public String type;
        public Set<String> groups;
        public String match;
        public Float above;
        public Float below;

        public JsonFormat(String type, Set<String> groups, String match, Float above, Float below) {
            this.type = type;
            this.groups = groups;
            this.match = match;
            this.above = above;
            this.below = below;
        }

    }

    public static class JsonSerializerDeserializer implements JsonSerializer<DietCondition>, JsonDeserializer<DietCondition> {
        public static final JsonSerializerDeserializer INSTANCE = new JsonSerializerDeserializer();

        @Override
        public JsonElement serialize(DietCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();

            JsonArray jsonArray = new JsonArray();

            src.groups.forEach(jsonArray::add);

            jsonObject.addProperty("type", DietCondition.TYPE.toString());
            jsonObject.add("groups", jsonArray);
            jsonObject.addProperty("match", src.matchMethod.toString());
            jsonObject.addProperty("above", src.above);
            jsonObject.addProperty("below", src.below);

            return jsonObject;
        }

        @Override
        public DietCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonFormat jsonFormat = DietMod.GSON.fromJson(json, JsonFormat.class);

            if (jsonFormat.type == null || jsonFormat.groups == null || jsonFormat.match == null || jsonFormat.above == null || jsonFormat.below == null) {
                throw new JsonSyntaxException("(DietCondition deserialize) A required attribute is missing!");
            }

            return new DietCondition(jsonFormat.groups, MatchMethod.findOrDefault(jsonFormat.match, MatchMethod.EVERY), jsonFormat.above, jsonFormat.below);
        }

    }
}
