package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.util.ListJS;

/**
 * @author LatvianModder
 */
public class IgnoredRecipeJS extends RecipeJS
{
	@Override
	public void create(ListJS args)
	{
		throw new RecipeExceptionJS("Can't create an ignored recipe!");
	}

	@Override
	public void deserialize()
	{
	}

	@Override
	public void serialize()
	{
	}
}