package me.nikyoff.diet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.nikyoff.diet.config.EffectConfig;
import me.nikyoff.diet.config.GroupConfig;
import me.nikyoff.diet.config.OverriddenFoodConfig;
import me.nikyoff.diet.effect.common.DietAttribute;
import me.nikyoff.diet.effect.common.DietCondition;
import me.nikyoff.diet.effect.common.DietStatusEffect;
import me.nikyoff.diet.effect.condition.ConditionDeserializerRegistry;
import me.nikyoff.diet.effect.condition.IDietCondition;
import me.nikyoff.diet.event.ServerEventHandlers;
import me.nikyoff.diet.network.NetworkHandlerC2S;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DietMod implements ModInitializer {
	public static final String MOD_ID = "diet";
	public static final Logger LOGGER = LogManager.getLogger("Diet");

	public static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(DietAttribute.class, new DietAttribute.JsonSerializerDeserializer())
			.registerTypeAdapter(DietStatusEffect.class, new DietStatusEffect.JsonSerializerDeserializer())
			.registerTypeAdapter(IDietCondition.class, new IDietCondition.JsonSerializerDeserializer())
			.create();

	public static final Tag<Item> INGREDIENTS = TagFactory.ITEM.create(DietMod.id("ingredients"));

	public static final Tag<Item> SPECIAL_FOOD = TagFactory.ITEM.create(DietMod.id("special_food"));

	public static Identifier id(String path) {
		return Identifier.tryParse(String.format("%s%s%s", DietMod.MOD_ID, Identifier.NAMESPACE_SEPARATOR, path));
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Initialize...");

		ConditionDeserializerRegistry.register(DietCondition.TYPE, new Pair(DietCondition.JsonSerializerDeserializer.INSTANCE, DietCondition.JsonSerializerDeserializer.INSTANCE));

		NetworkHandlerC2S.initialize();

		OverriddenFoodConfig.getInstance().initialize();
		GroupConfig.getInstance().initialize();
		EffectConfig.getInstance().initialize();

		ServerEventHandlers.initialize();

		LOGGER.info("Initialized!");
	}
}
