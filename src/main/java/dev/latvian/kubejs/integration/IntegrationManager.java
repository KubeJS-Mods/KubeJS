package dev.latvian.kubejs.integration;

import dev.latvian.kubejs.integration.gamestages.GameStagesIntegration;
import dev.latvian.kubejs.recipe.create.CreateRecipes;
import dev.latvian.kubejs.recipe.mekanism.MekanismRecipes;
import dev.latvian.kubejs.recipe.silentsmek.SilentsMechanismsRecipes;
import net.minecraftforge.common.MinecraftForge;
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
			MinecraftForge.EVENT_BUS.addListener(CreateRecipes::registerRecipeHandlers);
		}

		if (ModList.get().isLoaded("mekanism"))
		{
			MinecraftForge.EVENT_BUS.addListener(MekanismRecipes::registerRecipeHandlers);
		}

		if (ModList.get().isLoaded("silents_mechanisms"))
		{
			MinecraftForge.EVENT_BUS.addListener(SilentsMechanismsRecipes::registerRecipeHandlers);
		}
	}
}