package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.SmithingRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.StonecuttingRecipeJS;
import dev.latvian.kubejs.recipe.mod.BotanyPotsCropRecipeJS;
import dev.latvian.kubejs.recipe.mod.MATagRecipeJS;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;

/**
 * @author LatvianModder
 */
public class KubeJSRecipeEventHandler {
	public static void init() {
		RegisterRecipeHandlersEvent.EVENT.register(KubeJSRecipeEventHandler::registerRecipeHandlers);
	}

	private static void registerRecipeHandlers(RegisterRecipeHandlersEvent event) {
		event.registerShaped(new ResourceLocation("minecraft:crafting_shaped"));
		event.registerShapeless(new ResourceLocation("minecraft:crafting_shapeless"));
		event.register(new ResourceLocation("minecraft:stonecutting"), StonecuttingRecipeJS::new);
		event.register(new ResourceLocation("minecraft:smelting"), CookingRecipeJS::new);
		event.register(new ResourceLocation("minecraft:blasting"), CookingRecipeJS::new);
		event.register(new ResourceLocation("minecraft:smoking"), CookingRecipeJS::new);
		event.register(new ResourceLocation("minecraft:campfire_cooking"), CookingRecipeJS::new);
		event.register(new ResourceLocation("minecraft:smithing"), SmithingRecipeJS::new);

		// Mod recipe types that use vanilla syntax

		if (Platform.isModLoaded("cucumber")) {
			event.registerShaped(new ResourceLocation("cucumber:shaped_no_mirror"));
		}

		if (Platform.isModLoaded("mysticalagriculture")) {
			event.register("mysticalagriculture:tag", MATagRecipeJS::new);
		}

		if (Platform.isModLoaded("botanypots")) {
			event.register("botanypots:crop", BotanyPotsCropRecipeJS::new);
		}

		if (Platform.isModLoaded("extendedcrafting")) {
			event.registerShaped(new ResourceLocation("extendedcrafting:shaped_table"));
			event.registerShapeless(new ResourceLocation("extendedcrafting:shapeless_table"));
		}

		if (Platform.isModLoaded("dankstorage")) {
			event.registerShaped(new ResourceLocation("dankstorage:upgrade"));
		}
	}
}