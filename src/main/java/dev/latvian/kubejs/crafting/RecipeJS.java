package dev.latvian.kubejs.crafting;

import java.util.Map;

/**
 * @author LatvianModder
 */
public interface RecipeJS
{
	RecipeJS set(Map<String, Object> properties);

	void add();
}