package dev.latvian.kubejs.recipe.filter;

import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeJS;

/**
 * @author LatvianModder
 */
public class InputFilter implements RecipeFilter
{
	private final IngredientJS in;
	private final boolean exact;

	public InputFilter(IngredientJS i, boolean e)
	{
		in = i;
		exact = e;
	}

	@Override
	public boolean test(RecipeJS r)
	{
		return r.hasInput(in, exact);
	}
}
