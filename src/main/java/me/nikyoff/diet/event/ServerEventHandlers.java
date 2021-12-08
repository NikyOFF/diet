package me.nikyoff.diet.event;

import me.nikyoff.core.event.PlayerEntityEvents;
import me.nikyoff.diet.DietMod;
import me.nikyoff.diet.component.DietModComponents;
import me.nikyoff.diet.component.PlayerDietComponent;
import me.nikyoff.diet.config.EffectConfig;
import me.nikyoff.diet.config.GroupConfig;
import me.nikyoff.diet.util.DietValueGenerator;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.world.World;

public class ServerEventHandlers {

    public static void initialize() {
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(ServerEventHandlers::handleStartDataPackReload);

        ServerLifecycleEvents.SERVER_STARTED.register(ServerEventHandlers::handleServerStartedEvent);

        ServerPlayConnectionEvents.DISCONNECT.register(ServerEventHandlers::handleDisconnect);

        PlayerEntityEvents.FOOD_CONSUME.register(ServerEventHandlers::handleFoodConsume);
    }

    private static void handleStartDataPackReload(MinecraftServer minecraftServer, ServerResourceManager serverResourceManager) {
        GroupConfig.initialize();
        EffectConfig.initialize();
        DietValueGenerator.reload(minecraftServer);
    }

    private static void handleServerStartedEvent(MinecraftServer minecraftServer) {
        DietValueGenerator.reload(minecraftServer);
    }

    private static void handleDisconnect(ServerPlayNetworkHandler serverPlayNetworkHandler, MinecraftServer minecraftServer) {
        DietModComponents.PLAYER_DIET_COMPONENT.get(serverPlayNetworkHandler.player).cancelEffects();
    }

    private static void handleFoodConsume(PlayerEntity playerEntity, World world, ItemStack itemStack, int count) {
        DietMod.LOGGER.info("handleFoodConsume");

        PlayerDietComponent playerComponent = DietModComponents.PLAYER_DIET_COMPONENT.get(playerEntity);
        playerComponent.onConsume(itemStack);
    }
}
