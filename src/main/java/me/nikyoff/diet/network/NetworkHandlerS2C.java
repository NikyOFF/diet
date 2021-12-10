package me.nikyoff.diet.network;


import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NetworkHandlerS2C {
    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(GeneratedValuesSyncS2CPacket.IDENTIFIER, GeneratedValuesSyncS2CPacket::handleClient);
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncS2CPacket.EFFECT_CONFIG_SYNC_PACKET_IDENTIFIER, ConfigSyncS2CPacket::handleEffectConfig);
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncS2CPacket.GROUP_CONFIG_SYNC_PACKET_IDENTIFIER, ConfigSyncS2CPacket::handleGroupConfig);
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncS2CPacket.OVERRIDDEN_FOOD_SYNC_PACKET_IDENTIFIER, ConfigSyncS2CPacket::handleOverriddenFoodConfig);

    }
}
