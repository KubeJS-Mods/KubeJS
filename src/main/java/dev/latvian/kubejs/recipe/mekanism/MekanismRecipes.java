package dev.latvian.kubejs.recipe.mekanism;

import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;

/**
 * @author LatvianModder
 */
public class MekanismRecipes
{
	public static void registerRecipeHandlers(RegisterRecipeHandlersEvent event)
	{
		event.register("mekanism:crushing", MekanismMachineRecipeJS::new);
		event.register("mekanism:enriching", MekanismMachineRecipeJS::new);
		event.register("mekanism:purifying", MekanismMachineRecipeJS::new);
		event.register("mekanism:injecting", MekanismMachineRecipeJS::new);
		event.register("mekanism:metallurgic_infusing", MekanismMachineRecipeJS::new);
		event.register("mekanism:sawing", MekanismMachineRecipeJS::new);
	}
}