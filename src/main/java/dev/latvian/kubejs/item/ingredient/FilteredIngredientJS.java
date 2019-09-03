package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.ItemStackJS;

/**
 * @author LatvianModder
 */
public final class FilteredIngredientJS implements IngredientJS
{
	private final IngredientJS a;
	private final IngredientJS b;

	public FilteredIngredientJS(IngredientJS _a, IngredientJS _b)
	{
		a = _a;
		b = _b;
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return a.test(stack) && b.test(stack);
	}
}