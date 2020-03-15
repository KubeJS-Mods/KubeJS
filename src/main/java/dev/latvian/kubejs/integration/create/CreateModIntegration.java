package dev.latvian.kubejs.integration.create;

import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author LatvianModder
 */
public class CreateModIntegration
{
	public void init()
	{
		MinecraftForge.EVENT_BUS.addListener(this::registerRecipeHandlers);
	}

	private void registerRecipeHandlers(RegisterRecipeHandlersEvent event)
	{
		event.register(new RecipeTypeJS("create:crushing", CreateModMachineRecipeJS::new));
		event.register(new RecipeTypeJS("create:pressing", CreateModMachineRecipeJS::new));
		event.register(new RecipeTypeJS("create:splashing", CreateModMachineRecipeJS::new));
	}
}