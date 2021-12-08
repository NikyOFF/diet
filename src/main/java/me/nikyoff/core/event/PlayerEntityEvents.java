package me.nikyoff.core.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class PlayerEntityEvents {
    public static final Event<FoodPreConsume> FOOD_PRE_CONSUME = EventFactory.createArrayBacked(FoodPreConsume.class, callbacks -> (playerEntity, world, itemStack) -> {
        for (FoodPreConsume callback : callbacks) {
            ActionResult result = callback.onPreFoodConsume(playerEntity, world, itemStack);

            if(result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.SUCCESS;
    });

    public static final Event<FoodConsume> FOOD_CONSUME = EventFactory.createArrayBacked(FoodConsume.class, callbacks -> (playerEntity, world, itemStack, count) -> {
        for (FoodConsume callback : callbacks) {
            callback.onFoodConsume(playerEntity, world, itemStack, count);
        }
    });

    @FunctionalInterface
    public interface FoodPreConsume {
        ActionResult onPreFoodConsume(PlayerEntity playerEntity, World world, ItemStack itemStack);
    }

    @FunctionalInterface
    public interface FoodConsume {
        void onFoodConsume(PlayerEntity playerEntity, World world, ItemStack itemStack, Integer count);
    }
}
