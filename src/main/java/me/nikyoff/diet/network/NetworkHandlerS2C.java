package me.nikyoff.diet.network;


import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NetworkHandlerS2C {
    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(GeneratedValuesSyncS2CPacket.IDENTIFIER, GeneratedValuesSyncS2CPacket::handleClient);
    }
}
