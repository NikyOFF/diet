package me.nikyoff.core.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import me.nikyoff.core.CoreMod;

public class CoreModComponents implements EntityComponentInitializer {
    public static final ComponentKey<PlayerFoodDataComponent> PLAYER_FOOD_DATA_COMPONENT = ComponentRegistry.getOrCreate(CoreMod.id("player_food_data"), PlayerFoodDataComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(CoreModComponents.PLAYER_FOOD_DATA_COMPONENT, PlayerFoodDataComponent::new, RespawnCopyStrategy.ALWAYS_COPY);

    }
}
