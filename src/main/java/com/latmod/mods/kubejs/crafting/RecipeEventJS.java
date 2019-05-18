package com.latmod.mods.kubejs.crafting;

import com.latmod.mods.kubejs.events.EventJS;

/**
 * @author LatvianModder
 */
public class RecipeEventJS extends EventJS
{
	private final RecipeHandlerRegistry registry;

	public RecipeEventJS(RecipeHandlerRegistry r)
	{
		registry = r;
	}

	public IRecipeHandler get(String id)
	{
		return registry.get(id);
	}
}