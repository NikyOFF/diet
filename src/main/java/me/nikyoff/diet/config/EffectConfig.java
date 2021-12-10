package me.nikyoff.diet.config;

import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import me.nikyoff.diet.DietMod;
import me.nikyoff.diet.config.data.EffectConfigData;
import me.nikyoff.diet.effect.DietEffect;
import me.nikyoff.diet.effect.DietEffects;
import me.nikyoff.diet.effect.common.DietAttribute;
import me.nikyoff.diet.effect.common.DietCondition;
import me.nikyoff.diet.effect.common.DietStatusEffect;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;

import java.util.List;
import java.util.Set;

public class EffectConfig extends Config {

    private static EffectConfig instance = new EffectConfig();
    private static final EffectConfig defaultInstance = new EffectConfig(Set.of(
            new EffectConfigData(
                    List.of(new DietAttribute(EntityAttributes.GENERIC_MAX_HEALTH, EntityAttributeModifier.Operation.ADDITION, 2)),
                    List.of(new DietStatusEffect(StatusEffects.REGENERATION, 0)),
                    List.of(new DietCondition(Set.of("fruits", "vegetables", "proteins"), DietCondition.MatchMethod.ALL, 0.45f, 0.65f)),
                    DietEffect.MatchMethod.ALL
            )
    ));

    private final Set<EffectConfigData> effects = Sets.newHashSet();

    public EffectConfig() {
        super("effects");
    }

    public EffectConfig(Set<EffectConfigData> effects) {
        this();

        this.effects.clear();
        this.effects.addAll(effects);
    }

    public static EffectConfig getInstance() {
        return instance;
    }

    public Set<EffectConfigData> getEffects() {
        return effects;
    }

    @Override
    public EffectConfig getDefault() {
        return EffectConfig.defaultInstance;
    }

    @Override
    public void setInstance(Config config, boolean localConfig) {
        super.setInstance(config, localConfig);

        if (config instanceof EffectConfig) {
            EffectConfig.instance = (EffectConfig) config;

            DietEffects.build(EffectConfig.instance.effects.stream().toList());
        }
    }

    @Override
    public String toJson(boolean isDefault) {
        String json = DietMod.GSON.toJson(isDefault ? this.getDefault().effects : this.effects);

        return json;
    }

    @Override
    public EffectConfig fromJson(String json) {
        Set<EffectConfigData> effects = DietMod.GSON.fromJson(json, new TypeToken<Set<EffectConfigData>>(){}.getType());
        return new EffectConfig(effects);
    }
}
