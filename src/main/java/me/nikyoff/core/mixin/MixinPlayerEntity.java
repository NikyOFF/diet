package me.nikyoff.core.mixin;

import me.nikyoff.core.api.IPlayerFoodDataComponent;
import me.nikyoff.core.component.CoreModComponents;
import me.nikyoff.core.event.PlayerEntityEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {
    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "eatFood", at = @At("HEAD"), cancellable = true)
    public void eatFoodInject(World world, ItemStack itemStack, CallbackInfoReturnable<ItemStack> callbackInfoReturnable) {
        if (!itemStack.isFood()) {
            return;
        }

        ActionResult result = PlayerEntityEvents.FOOD_PRE_CONSUME.invoker().onPreFoodConsume((PlayerEntity) (Object) this, world, itemStack);

        if (!result.isAccepted()) {
            callbackInfoReturnable.setReturnValue(itemStack);
        }

        IPlayerFoodDataComponent playerFoodDataComponent = CoreModComponents.PLAYER_FOOD_DATA_COMPONENT.get(this);
        Item item = itemStack.getItem();

        Integer count = playerFoodDataComponent.onConsume(item).orElse(1);

        PlayerEntityEvents.FOOD_CONSUME.invoker().onFoodConsume((PlayerEntity) (Object) this, world, itemStack, count);
    }
}
