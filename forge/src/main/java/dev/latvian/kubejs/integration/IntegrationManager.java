package dev.latvian.kubejs.integration;

import dev.latvian.kubejs.integration.gamestages.GameStagesIntegration;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.recipe.create.CreateRecipes;
import dev.latvian.kubejs.recipe.mekanism.MekanismRecipes;
import dev.latvian.kubejs.recipe.silentsmek.SilentsMechanismsRecipes;
import net.minecraftforge.fml.ModList;

/**
 * @author LatvianModder
 */
public class IntegrationManager
{
	public static void init()
	{
		if (ModList.get().isLoaded("gamestages"))
		{
			new GameStagesIntegration().init();
		}

		if (ModList.get().isLoaded("create"))
		{
			RegisterRecipeHandlersEvent.EVENT.register(CreateRecipes::registerRecipeHandlers);
		}

		if (ModList.get().isLoaded("mekanism"))
		{
			RegisterRecipeHandlersEvent.EVENT.register(MekanismRecipes::registerRecipeHandlers);
		}

		if (ModList.get().isLoaded("silents_mechanisms"))
		{
			RegisterRecipeHandlersEvent.EVENT.register(SilentsMechanismsRecipes::registerRecipeHandlers);
		}
	}
}