package me.nikyoff.diet.network;

import me.nikyoff.diet.DietMod;
import me.nikyoff.diet.config.EffectConfig;
import me.nikyoff.diet.config.GroupConfig;
import me.nikyoff.diet.config.OverriddenFoodConfig;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ConfigSyncS2CPacket {
    public static Identifier EFFECT_CONFIG_SYNC_PACKET_IDENTIFIER = DietMod.id("sync_effect_config");
    public static Identifier GROUP_CONFIG_SYNC_PACKET_IDENTIFIER = DietMod.id("sync_group_config");
    public static Identifier OVERRIDDEN_FOOD_SYNC_PACKET_IDENTIFIER = DietMod.id("sync_overridden_food_config");

    public static PacketByteBuf write(EffectConfig config) {
        PacketByteBuf packetByteBuf = PacketByteBufs.create();

        packetByteBuf.writeString(config.toJson(false));

        return packetByteBuf;
    }

    public static PacketByteBuf write(GroupConfig config) {
        PacketByteBuf packetByteBuf = PacketByteBufs.create();

        packetByteBuf.writeString(config.toJson(false));

        return packetByteBuf;
    }

    public static PacketByteBuf write(OverriddenFoodConfig config) {
        PacketByteBuf packetByteBuf = PacketByteBufs.create();

        packetByteBuf.writeString(config.toJson(false));

        return packetByteBuf;
    }

    public static void sendEffectConfig(ServerPlayerEntity serverPlayerEntity) {
        ServerPlayNetworking.send(serverPlayerEntity, ConfigSyncS2CPacket.EFFECT_CONFIG_SYNC_PACKET_IDENTIFIER, write(EffectConfig.getInstance()));
    }

    public static void sendGroupConfig(ServerPlayerEntity serverPlayerEntity) {
        ServerPlayNetworking.send(serverPlayerEntity, ConfigSyncS2CPacket.GROUP_CONFIG_SYNC_PACKET_IDENTIFIER, write(GroupConfig.getInstance()));
    }

    public static void sendOverriddenFoodConfig(ServerPlayerEntity serverPlayerEntity) {
        ServerPlayNetworking.send(serverPlayerEntity, ConfigSyncS2CPacket.OVERRIDDEN_FOOD_SYNC_PACKET_IDENTIFIER, write(OverriddenFoodConfig.getInstance()));
    }

    public static void send(ServerPlayerEntity serverPlayerEntity) {
        ConfigSyncS2CPacket.sendEffectConfig(serverPlayerEntity);
        ConfigSyncS2CPacket.sendGroupConfig(serverPlayerEntity);
        ConfigSyncS2CPacket.sendOverriddenFoodConfig(serverPlayerEntity);
    }

    public static void sync(MinecraftServer minecraftServer) {
        minecraftServer.getPlayerManager().getPlayerList().forEach(ConfigSyncS2CPacket::send);
    }

    //region client handle
    public static void handleEffectConfig(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        String json = packetByteBuf.readString();

        EffectConfig config = EffectConfig.getInstance().fromJson(json);

        minecraftClient.execute(() -> {
            EffectConfig.getInstance().setInstance(config, true);
        });
    }

    public static void handleGroupConfig(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        String json = packetByteBuf.readString();

        GroupConfig config = GroupConfig.getInstance().fromJson(json);

        minecraftClient.execute(() -> {
            GroupConfig.getInstance().setInstance(config, true);
        });
    }

    public static void handleOverriddenFoodConfig(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        String json = packetByteBuf.readString();

        OverriddenFoodConfig config = OverriddenFoodConfig.getInstance().fromJson(json);

        minecraftClient.execute(() -> {
            OverriddenFoodConfig.getInstance().setInstance(config, true);
        });
    }
    //endregion
}
