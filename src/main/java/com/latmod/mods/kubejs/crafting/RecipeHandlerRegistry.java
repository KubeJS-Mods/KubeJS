package com.latmod.mods.kubejs.crafting;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class RecipeHandlerRegistry
{
	public static final RecipeHandlerRegistry INSTANCE = new RecipeHandlerRegistry();

	private final Map<String, IRecipeHandler> map = new HashMap<>();

	public IRecipeHandler get(String id)
	{
		return map.get(id);
	}

	void register(String id, IRecipeHandler handler)
	{
		map.put(id, handler);
	}
}