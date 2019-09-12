package dev.latvian.kubejs.crafting;

import java.util.Map;

/**
 * @author LatvianModder
 */
public abstract class RecipeBaseJS
{
	public abstract RecipeBaseJS set(Map<String, Object> properties);

	public abstract void add();
}