package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.ListJS;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
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

	public MatchAnyIngredientJS add(@Nullable Object ingredient)
	{
		IngredientJS i = IngredientJS.of(ingredient);

		if (i != EmptyItemStackJS.INSTANCE)
		{
			ingredients.add(i);
		}

		return this;
	}

	public MatchAnyIngredientJS addAll(Object ingredients)
	{
		for (Object o : ListJS.orSelf(ingredients))
		{
			add(o);
		}

		return this;
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
		ingredients.add(ingredient);
	}

	@Override
	public String toString()
	{
		if (ingredients.isEmpty())
		{
			return "[]";
		}

		Iterator<IngredientJS> it = ingredients.iterator();
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		while (true)
		{
			IngredientJS e = it.next();
			sb.append(e);

			if (!it.hasNext())
			{
				return sb.append(']').toString();
			}

			sb.append('|');
		}
	}
}