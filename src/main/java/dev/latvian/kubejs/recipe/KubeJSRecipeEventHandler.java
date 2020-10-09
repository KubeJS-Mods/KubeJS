package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.SmithingRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.StonecuttingRecipeJS;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author LatvianModder
 */
public class KubeJSRecipeEventHandler
{
	public void init()
	{
		MinecraftForge.EVENT_BUS.addListener(this::registerRecipeHandlers);
	}

	private void registerRecipeHandlers(RegisterRecipeHandlersEvent event)
	{
		event.register("forge:conditional", ConditionalRecipeJS::new);
		event.register("minecraft:crafting_shaped", ShapedRecipeJS::new);
		event.register("minecraft:crafting_shapeless", ShapelessRecipeJS::new);
		event.register("minecraft:stonecutting", StonecuttingRecipeJS::new);
		event.register("minecraft:smelting", CookingRecipeJS::new);
		event.register("minecraft:blasting", CookingRecipeJS::new);
		event.register("minecraft:smoking", CookingRecipeJS::new);
		event.register("minecraft:campfire_cooking", CookingRecipeJS::new);
		event.register("minecraft:smithing", SmithingRecipeJS::new);
	}
}