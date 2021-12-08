package me.nikyoff.core.component;

import com.google.common.collect.Maps;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import me.nikyoff.core.api.IPlayerFoodDataComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlayerFoodDataComponent implements IPlayerFoodDataComponent, AutoSyncedComponent {
    private final PlayerEntity playerEntityProvider;

    private final Map<Item, Integer> foodEaten = Maps.newHashMap();

    public PlayerFoodDataComponent(PlayerEntity playerEntityProvider) {
        this.playerEntityProvider = playerEntityProvider;
    }

    public PlayerEntity getPlayerEntityProvider() {
        return playerEntityProvider;
    }

    @Override
    public Map<Item, Integer> getFoodEaten() {
        return this.foodEaten;
    }

    @Override
    public void setFoodEaten(Map<Item, Integer> foodEaten) {
        this.foodEaten.clear();
        this.foodEaten.putAll(foodEaten);
        CoreModComponents.PLAYER_FOOD_DATA_COMPONENT.sync(this.playerEntityProvider);
    }

    @Override
    public Integer getFoodEatenCount(Item item) {
        return Math.max(0, this.foodEaten.getOrDefault(item, 0));
    }

    @Override
    public Optional<Integer> onConsume(Item item) {
        if (!item.isFood()) {
            return Optional.empty();
        }

        int count = 1;

        if (this.foodEaten.containsKey(item)) {
            count += this.foodEaten.get(item);
        }

        this.foodEaten.put(item, count);
        CoreModComponents.PLAYER_FOOD_DATA_COMPONENT.sync(this.playerEntityProvider);
        return Optional.of(count);
    }

    //region sync
    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.playerEntityProvider;
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeMap(this.foodEaten, (packetByteBuf, item) -> packetByteBuf.writeString(Registry.ITEM.getId(item).toString()), PacketByteBuf::writeInt);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        Map<Item, Integer> foodEaten = buf.readMap(PacketByteBuf::readString, PacketByteBuf::readInt)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                            entry -> Registry.ITEM.get(new Identifier(entry.getKey())),
                            Map.Entry::getValue
                        )
                );

        this.foodEaten.clear();

        this.foodEaten.putAll(foodEaten);
    }
    //endregion

    //region NBT
    @Override
    public void readFromNbt(NbtCompound tag) {
        if (tag.contains(Nbt.FOOD_EATEN.getKey())) {
            NbtCompound foodEatenCompound = tag.getCompound(Nbt.FOOD_EATEN.getKey());

            for (String key : foodEatenCompound.getKeys()) {
                Item item = Registry.ITEM.get(new Identifier(key));
                Integer eatenCount = foodEatenCompound.getInt(key);
                this.foodEaten.put(item, eatenCount);
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        if (!this.foodEaten.isEmpty()) {
            NbtCompound foodEatenCompound = new NbtCompound();

            for (Map.Entry<Item, Integer> entry : this.foodEaten.entrySet()) {
                Registry.ITEM.getKey(entry.getKey()).ifPresent(registryKey -> foodEatenCompound.putInt(registryKey.getValue().toString(), entry.getValue()));
            }

            tag.put(Nbt.FOOD_EATEN.getKey(), foodEatenCompound);
        }
    }

    public enum Nbt {
        FOOD_EATEN("food_eaten");

        private final String key;

        Nbt(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }
    //endregion
}
