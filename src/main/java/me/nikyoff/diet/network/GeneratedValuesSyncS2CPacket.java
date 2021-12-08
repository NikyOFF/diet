package me.nikyoff.diet.network;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.nikyoff.diet.DietMod;
import me.nikyoff.diet.api.IDietGroup;
import me.nikyoff.diet.group.DietGroups;
import me.nikyoff.diet.util.DietValueGenerator;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GeneratedValuesSyncS2CPacket {
    public static Identifier IDENTIFIER = DietMod.id("generated_values_sync");
    private static final Gson gson = new GsonBuilder().create();

    public static void send(MinecraftServer minecraftServer, Map<Item, Set<IDietGroup>> generated) {
        PacketByteBuf packetByteBuf = PacketByteBufs.create();
        Map<String, Set<String>> map = Maps.newHashMap();

        for (Map.Entry<Item, Set<IDietGroup>> entry : generated.entrySet()) {
            map.put(Registry.ITEM.getId(entry.getKey()).toString(), entry.getValue().stream().map(IDietGroup::getName).collect(Collectors.toSet()));
        }

        packetByteBuf.writeString(GeneratedValuesSyncS2CPacket.gson.toJson(map));

        for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(serverPlayerEntity, GeneratedValuesSyncS2CPacket.IDENTIFIER, packetByteBuf);
        }
    }

    public static void handleClient(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        String jsonMap = packetByteBuf.readString();
        Map<Item, Set<IDietGroup>> generated = new HashMap<>();
        Type type = new TypeToken<Map<String, Set<String>>>(){}.getType();
        Map<String, Set<String>> map = GeneratedValuesSyncS2CPacket.gson.fromJson(jsonMap, type);
        Map<String, IDietGroup> groups = new HashMap<>();

        for (IDietGroup group : DietGroups.get()) {
            groups.put(group.getName(), group);
        }

        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            Set<String> value = entry.getValue();

            Item item = Registry.ITEM.get(new Identifier(key));
            Set<IDietGroup> dietGroups = Sets.newHashSet();

            for (String groupName : value) {
                IDietGroup group = groups.get(groupName);

                if (group != null) {
                    dietGroups.add(group);
                }
            }

            generated.put(item, dietGroups);
        }

        minecraftClient.execute(() -> {
            DietValueGenerator.putAll(generated);
        });
    }
}
