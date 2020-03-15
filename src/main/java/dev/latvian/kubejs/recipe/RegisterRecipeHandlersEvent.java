package dev.latvian.kubejs.recipe;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class RegisterRecipeHandlersEvent extends Event
{
	private final Map<ResourceLocation, RecipeTypeJS> map;

	public RegisterRecipeHandlersEvent(Map<ResourceLocation, RecipeTypeJS> m)
	{
		map = m;
	}

	public void register(RecipeTypeJS type)
	{
		map.put(type.serializer.getRegistryName(), type);
	}
}