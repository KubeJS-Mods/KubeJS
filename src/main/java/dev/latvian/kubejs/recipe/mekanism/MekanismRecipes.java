package dev.latvian.kubejs.recipe.mekanism;

import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;

/**
 * @author LatvianModder
 */
public class MekanismRecipes
{
	public static void registerRecipeHandlers(RegisterRecipeHandlersEvent event)
	{
		event.register(new RecipeTypeJS("mekanism:crushing", MekanismMachineRecipeJS::new));
		event.register(new RecipeTypeJS("mekanism:enriching", MekanismMachineRecipeJS::new));
		event.register(new RecipeTypeJS("mekanism:purifying", MekanismMachineRecipeJS::new));
		event.register(new RecipeTypeJS("mekanism:injecting", MekanismMachineRecipeJS::new));
		event.register(new RecipeTypeJS("mekanism:metallurgic_infusing", MekanismMachineRecipeJS::new));
		event.register(new RecipeTypeJS("mekanism:sawing", MekanismMachineRecipeJS::new));
	}
}