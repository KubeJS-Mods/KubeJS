package dev.latvian.kubejs.recipe;

import net.minecraftforge.eventbus.api.Event;

/**
 * @author LatvianModder
 */
public class RegisterRecipeHandlersEvent extends Event
{
	private final RecipeEventJS event;

	public RegisterRecipeHandlersEvent(RecipeEventJS e)
	{
		event = e;
	}

	public void register(RecipeTypeJS type)
	{
		event.deserializerMap.put(type.id, new RecipeFunction(event, type));
	}
}