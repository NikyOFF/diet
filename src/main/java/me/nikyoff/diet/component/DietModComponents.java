package me.nikyoff.diet.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import me.nikyoff.diet.DietMod;

public class DietModComponents implements EntityComponentInitializer {
    public static final ComponentKey<PlayerDietComponent> PLAYER_DIET_COMPONENT = ComponentRegistry.getOrCreate(DietMod.id("player"), PlayerDietComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(DietModComponents.PLAYER_DIET_COMPONENT, PlayerDietComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
