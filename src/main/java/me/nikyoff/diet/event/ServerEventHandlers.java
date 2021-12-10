package me.nikyoff.diet.event;

import me.nikyoff.core.event.PlayerEntityEvents;
import me.nikyoff.diet.component.DietModComponents;
import me.nikyoff.diet.component.PlayerDietComponent;
import me.nikyoff.diet.config.EffectConfig;
import me.nikyoff.diet.config.GroupConfig;
import me.nikyoff.diet.config.OverriddenFoodConfig;
import me.nikyoff.diet.network.ConfigSyncS2CPacket;
import me.nikyoff.diet.network.GeneratedValuesSyncS2CPacket;
import me.nikyoff.diet.util.DietValueGenerator;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.world.World;

public class ServerEventHandlers {

    public static void initialize() {
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(ServerEventHandlers::handleStartDataPackReloadEvent);

        ServerLifecycleEvents.SERVER_STARTED.register(ServerEventHandlers::handleServerStartedEvent);

        ServerPlayConnectionEvents.JOIN.register(ServerEventHandlers::handleJoinEvent);

        ServerPlayConnectionEvents.DISCONNECT.register(ServerEventHandlers::handleDisconnectEvent);

        PlayerEntityEvents.FOOD_CONSUME.register(ServerEventHandlers::handleFoodConsumeEvent);
    }

    private static void handleStartDataPackReloadEvent(MinecraftServer minecraftServer, ServerResourceManager serverResourceManager) {
        OverriddenFoodConfig.getInstance().initialize();
        GroupConfig.getInstance().initialize();
        EffectConfig.getInstance().initialize();

        DietValueGenerator.reload(minecraftServer);

        if (!minecraftServer.isSingleplayer()) {
            GeneratedValuesSyncS2CPacket.sync(minecraftServer);
            ConfigSyncS2CPacket.sync(minecraftServer);
        }
    }

    private static void handleServerStartedEvent(MinecraftServer minecraftServer) {
        DietValueGenerator.reload(minecraftServer);
    }

    private static void handleJoinEvent(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        if (!minecraftServer.isSingleplayer()) {
            ConfigSyncS2CPacket.sync(minecraftServer);
            GeneratedValuesSyncS2CPacket.sync(minecraftServer);
        }
    }

    private static void handleDisconnectEvent(ServerPlayNetworkHandler serverPlayNetworkHandler, MinecraftServer minecraftServer) {
        DietModComponents.PLAYER_DIET_COMPONENT.get(serverPlayNetworkHandler.player).cancelEffects();
    }

    private static void handleFoodConsumeEvent(PlayerEntity playerEntity, World world, ItemStack itemStack, int count) {
        PlayerDietComponent playerComponent = DietModComponents.PLAYER_DIET_COMPONENT.get(playerEntity);
        playerComponent.onConsume(itemStack);
    }
}
