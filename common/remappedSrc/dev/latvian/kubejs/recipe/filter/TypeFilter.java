package dev.latvian.kubejs.recipe.filter;

import dev.latvian.kubejs.recipe.RecipeJS;

/**
 * @author LatvianModder
 */
public class TypeFilter implements RecipeFilter
{
	private final String type;

	public TypeFilter(String t)
	{
		type = t;
	}

	@Override
	public boolean test(RecipeJS r)
	{
		return r.type.toString().equals(type);
	}
}
