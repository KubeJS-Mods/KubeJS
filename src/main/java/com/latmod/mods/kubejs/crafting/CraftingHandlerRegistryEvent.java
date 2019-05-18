package com.latmod.mods.kubejs.crafting;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author LatvianModder
 */
public class CraftingHandlerRegistryEvent extends Event
{
	private final RecipeHandlerRegistry registry;

	public CraftingHandlerRegistryEvent(RecipeHandlerRegistry r)
	{
		registry = r;
	}

	public void register(String id, IRecipeHandler handler)
	{
		registry.register(id, handler);
	}
}