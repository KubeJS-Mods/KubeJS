package dev.latvian.kubejs.recipe.filter;

import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeJS;

/**
 * @author LatvianModder
 */
public class OutputFilter implements RecipeFilter
{
	private final IngredientJS out;
	private final boolean exact;

	public OutputFilter(IngredientJS o, boolean e)
	{
		out = o;
		exact = e;
	}

	@Override
	public boolean test(RecipeJS r)
	{
		return r.hasOutput(out, exact);
	}

	@Override
	public String toString()
	{
		return "OutputFilter{" +
				"out=" + out +
				", exact=" + exact +
				'}';
	}
}
