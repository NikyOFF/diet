package me.nikyoff.diet.client;

import me.nikyoff.diet.DietMod;
import me.nikyoff.diet.client.gui.DietScreen;
import me.nikyoff.diet.network.NetworkHandlerS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class DietModClient implements ClientModInitializer {
    private static KeyBinding openDietGuiKeyBind = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    String.format("%s.key.gui", DietMod.MOD_ID),
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_Z,
                    String.format("%s.keybinding.category", DietMod.MOD_ID)
            )
    );

    @Override
    public void onInitializeClient() {
        NetworkHandlerS2C.initialize();

        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            while (DietModClient.openDietGuiKeyBind.wasPressed()) {
                if (minecraftClient.currentScreen == null) {
                    minecraftClient.setScreen(new DietScreen(minecraftClient.player));
                }
            }
        });

//        TabsApiImpl.getInstance().addTab(new Tab(Items.APPLE, 0, () -> {
//            MinecraftClient minecraftClient = MinecraftClient.getInstance();
//
//            if (minecraftClient.player == null) {
//                return null;
//            }
//
//            return new DietScreen(minecraftClient.player);
//        }));
    }
}
