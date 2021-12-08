package me.nikyoff.core;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CoreMod implements ModInitializer {
    public static final String MOD_ID = "core";
    public static final Logger LOGGER = LogManager.getLogger("Core");

    public static Identifier id(String path) {
        return Identifier.tryParse(String.format("%s%s%s", CoreMod.MOD_ID, Identifier.NAMESPACE_SEPARATOR, path));
    }

    @Override
    public void onInitialize() {
        CoreMod.LOGGER.info("Initialize");
    }
}
