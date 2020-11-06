package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.ItemStackJS;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author LatvianModder
 */
public final class FilteredIngredientJS implements IngredientJS
{
	private final IngredientJS ingredient;
	private final IngredientJS filter;

	public FilteredIngredientJS(IngredientJS i, IngredientJS f)
	{
		ingredient = i;
		filter = f;
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return ingredient.test(stack) && filter.test(stack);
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		Set<ItemStackJS> set = new LinkedHashSet<>();

		for (ItemStackJS stack : ingredient.getStacks())
		{
			if (filter.test(stack))
			{
				set.add(stack);
			}
		}

		return set;
	}
}