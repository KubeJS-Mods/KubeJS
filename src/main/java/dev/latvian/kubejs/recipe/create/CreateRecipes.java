package dev.latvian.kubejs.recipe.create;

import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;

/**
 * @author LatvianModder
 */
public class CreateRecipes
{
	public static void registerRecipeHandlers(RegisterRecipeHandlersEvent event)
	{
		event.register(new RecipeTypeJS("create:crushing", CreateMachineRecipeJS::new));
		event.register(new RecipeTypeJS("create:pressing", CreateMachineRecipeJS::new));
		event.register(new RecipeTypeJS("create:splashing", CreateMachineRecipeJS::new));
	}
}