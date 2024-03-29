package me.nikyoff.diet.mixin;

import me.nikyoff.diet.util.DietItemTooltip;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public abstract class MixinItem {

    @Inject(method = "appendTooltip", at = @At("HEAD"))
    public void appendTooltipInject(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo callbackInfo) {
        if (world == null) {
            return;
        }

        DietItemTooltip.applyTooltip(stack, tooltip);
    }
}
