package dev.latvian.kubejs.integration.create;

import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import net.minecraft.util.ResourceLocation;
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
		event.register(new CreateModMachineRecipeTypeJS(new ResourceLocation("create:crushing")));
		event.register(new CreateModMachineRecipeTypeJS(new ResourceLocation("create:pressing")));
		event.register(new CreateModMachineRecipeTypeJS(new ResourceLocation("create:splashing")));
	}
}