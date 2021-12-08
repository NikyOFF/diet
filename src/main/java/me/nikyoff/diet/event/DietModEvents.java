package me.nikyoff.diet.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public class DietModEvents {
    public static final Event<ApplyDecay> APPLY_DECAY = EventFactory.createArrayBacked(ApplyDecay.class, callbacks -> (playerEntity) -> {

        for (ApplyDecay callback : callbacks) {
            ActionResult result = callback.applyDecay(playerEntity);

            if(result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.FAIL;
    });

    @FunctionalInterface
    public interface ApplyDecay {
        ActionResult applyDecay(PlayerEntity playerEntity);
    }

}
