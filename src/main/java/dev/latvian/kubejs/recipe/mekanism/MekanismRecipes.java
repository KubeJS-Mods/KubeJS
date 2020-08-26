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
		event.register("mekanism:purifying", () -> new MekanismMachineRecipeJS("itemInput", "output"));
		event.register("mekanism:injecting", () -> new MekanismMachineRecipeJS("itemInput", "output"));
		event.register("mekanism:metallurgic_infusing", () -> new MekanismMachineRecipeJS("itemInput", "output"));
		event.register("mekanism:sawing", () -> new MekanismMachineRecipeJS("itemInput", "mainOutput"));
		event.register("mekanism:combining", MekanismCombiningRecipeJS::new);
	}
}