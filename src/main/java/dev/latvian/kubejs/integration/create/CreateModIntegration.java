package dev.latvian.kubejs.integration.create;

import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author LatvianModder
 */
public class CreateModIntegration
{
	public void init()
	{
		MinecraftForge.EVENT_BUS.addListener(this::registerRecipeHanlders);
	}

	private void registerRecipeHanlders(RegisterRecipeHandlersEvent event)
	{
		event.register(CreateModCrushingRecipeJS.TYPE);
		event.register(CreateModPressingRecipeJS.TYPE);
		event.register(CreateModSplashingRecipeJS.TYPE);
	}
}