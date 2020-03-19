package dev.latvian.kubejs.recipe.silentsmek;

import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;

/**
 * @author LatvianModder
 */
public class SilentsMechanismsRecipes
{
	public static void registerRecipeHandlers(RegisterRecipeHandlersEvent event)
	{
		event.register(new RecipeTypeJS("silents_mechanisms:alloy_smelting", SilentsMechanmismsAlloySmeltingRecipeJS::new));
	}
}