package me.nikyoff.diet.effect;

import com.google.common.collect.Sets;
import me.nikyoff.diet.config.data.EffectConfigData;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DietEffects {
    private static final String UUID_PREFIX = "ea4130c8-9065-48a6-9207-ddc020fb9fc8";

    private static final Set<DietEffect> effects = Sets.newHashSet();

    public static Set<DietEffect> getEffects() {
        return effects;
    }

    public static void build(final List<EffectConfigData> configs) {
        DietEffects.effects.clear();

        for (int i = 0; i < configs.size(); i++) {
            EffectConfigData effectConfigData = configs.get(i);

            DietEffect dietEffect = new DietEffect(
                    UUID.nameUUIDFromBytes((DietEffects.UUID_PREFIX + i).getBytes()),
                    effectConfigData.getAttributes(),
                    effectConfigData.getStatusEffects(),
                    effectConfigData.getDietConditions(),
                    DietEffect.MatchMethod.findOrDefault(effectConfigData.getMatchMethod(), DietEffect.MatchMethod.ALL)
            );

            DietEffects.effects.add(dietEffect);
        }

    }

}
