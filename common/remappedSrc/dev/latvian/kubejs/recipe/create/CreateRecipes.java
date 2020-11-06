package dev.latvian.kubejs.recipe.create;

import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;

/**
 * @author LatvianModder
 */
public class CreateRecipes
{
	public static void registerRecipeHandlers(RegisterRecipeHandlersEvent event)
	{
		event.register("create:crushing", CreateMachineRecipeJS::new);
		event.register("create:pressing", CreateMachineRecipeJS::new);
		event.register("create:splashing", CreateMachineRecipeJS::new);
	}
}