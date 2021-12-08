package me.nikyoff.diet.effect;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.nikyoff.diet.DietMod;
import me.nikyoff.diet.effect.common.DietAttribute;
import me.nikyoff.diet.effect.common.DietStatusEffect;
import me.nikyoff.diet.effect.condition.IDietCondition;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class DietEffect {

    @Expose
    private final UUID uuid;

    private final List<DietAttribute> attributes;

    @SerializedName("status_effects")
    private final List<DietStatusEffect> statusEffects;

    @SerializedName("diet_conditions")
    private final List<IDietCondition> dietConditions;

    @SerializedName("match_method")
    private final MatchMethod matchMethod;

    public DietEffect(UUID uuid, List<DietAttribute> attributes, List<DietStatusEffect> statusEffects, List<IDietCondition> dietConditions, MatchMethod matchMethod) {
        this.uuid = uuid;
        this.attributes = attributes;
        this.statusEffects = statusEffects;
        this.dietConditions = dietConditions;
        this.matchMethod = matchMethod;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<DietAttribute> getAttributes() {
        return attributes;
    }

    public List<DietStatusEffect> getStatusEffects() {
        return statusEffects;
    }

    public List<IDietCondition> getDietConditions() {
        return dietConditions;
    }

    public int getMatches(PlayerEntity playerEntity) {
        return this.matchMethod.getMatches(playerEntity, this.dietConditions);
    }

    public enum MatchMethod {
        EVERY {

            @Override
            int getMatches(PlayerEntity playerEntity, List<IDietCondition> conditions) {
                int count = 0;

                for (IDietCondition dietCondition : conditions) {
                    int matches = dietCondition.getMatches(playerEntity);

                    if (matches > 0) {
                        count++;
                    }
                }

                return count;
            }

        },
        ANY {

            @Override
            int getMatches(PlayerEntity playerEntity, List<IDietCondition> conditions) {

                for (IDietCondition dietCondition : conditions) {
                    int matches = dietCondition.getMatches(playerEntity);

                    if (matches > 0) {
                        return 1;
                    }
                }

                return 0;
            }

        },
        AVERAGE {

            @Override
            int getMatches(PlayerEntity playerEntity, List<IDietCondition> conditions) {
                int count = 0;

                for (IDietCondition dietCondition : conditions) {
                    int matches = dietCondition.getMatches(playerEntity);

                    if (matches > 0) {
                        count++;
                    }
                }

                return count >= (conditions.size() / 2.0f) ? 1 : 0;
            }

        },
        ALL {

            @Override
            int getMatches(PlayerEntity playerEntity, List<IDietCondition> conditions) {

                for (IDietCondition dietCondition : conditions) {
                    int matches = dietCondition.getMatches(playerEntity);

                    if (matches == 0) {
                        return 0;
                    }
                }

                return 1;
            }

        },
        NONE {

            @Override
            int getMatches(PlayerEntity playerEntity, List<IDietCondition> conditions) {

                for (IDietCondition dietCondition : conditions) {
                    int matches = dietCondition.getMatches(playerEntity);

                    if (matches > 0) {
                        return 0;
                    }
                }

                return 1;
            }

        };

        abstract int getMatches(PlayerEntity playerEntity, List<IDietCondition> conditions);

        public static MatchMethod findOrDefault(String name, MatchMethod def) {
            try {
                return MatchMethod.valueOf(name.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                DietMod.LOGGER.error("No such match method {}", name);
            }

            return def;
        }
    }
}
