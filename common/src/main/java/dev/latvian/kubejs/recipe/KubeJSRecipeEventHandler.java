package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.SmithingRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.StonecuttingRecipeJS;
import me.shedaniel.architectury.platform.Platform;

/**
 * @author LatvianModder
 */
public class KubeJSRecipeEventHandler
{
	public static void init()
	{
		RegisterRecipeHandlersEvent.EVENT.register(KubeJSRecipeEventHandler::registerRecipeHandlers);
	}

	private static void registerRecipeHandlers(RegisterRecipeHandlersEvent event)
	{
		if (Platform.isForge())
		{
			event.register("forge:conditional", ConditionalRecipeJS::new);
		}

		event.register("minecraft:crafting_shaped", ShapedRecipeJS::new);
		event.register("minecraft:crafting_shapeless", ShapelessRecipeJS::new);
		event.register("minecraft:stonecutting", StonecuttingRecipeJS::new);
		event.register("minecraft:smelting", CookingRecipeJS::new);
		event.register("minecraft:blasting", CookingRecipeJS::new);
		event.register("minecraft:smoking", CookingRecipeJS::new);
		event.register("minecraft:campfire_cooking", CookingRecipeJS::new);
		event.register("minecraft:smithing", SmithingRecipeJS::new);

		// Mod recipe types that use vanilla syntax

		event.register("cucumber:shaped_no_mirror", ShapedRecipeJS::new);
	}
}