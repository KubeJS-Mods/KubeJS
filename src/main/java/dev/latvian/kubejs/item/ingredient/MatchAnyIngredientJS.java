package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.ItemStackJS;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class MatchAnyIngredientJS implements IngredientJS, Consumer<IngredientJS>
{
	public final List<IngredientJS> ingredients = new ArrayList<>();

	public void add(IngredientJS ingredient)
	{
		ingredients.add(ingredient);
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		if (stack.isEmpty())
		{
			return false;
		}

		for (IngredientJS ingredient : ingredients)
		{
			if (ingredient.test(stack))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		Set<ItemStackJS> set = new LinkedHashSet<>();

		for (IngredientJS ingredient : ingredients)
		{
			set.addAll(ingredient.getStacks());
		}

		return set;
	}

	@Override
	public boolean isEmpty()
	{
		for (IngredientJS i : ingredients)
		{
			if (!i.isEmpty())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public void accept(IngredientJS ingredient)
	{
		add(ingredient);
	}
}