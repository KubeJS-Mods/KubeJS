package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.type.RecipeJS;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class RecipeCollection implements IRecipeCollection
{
	public final List<RecipeJS> list;
	public Consumer<RecipeJS> recipeChanged;

	public RecipeCollection(List<RecipeJS> l)
	{
		list = l;
	}

	@Override
	public void remove()
	{
		for (RecipeJS r : list)
		{
			r.remove();
		}
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public boolean hasInput(Object ingredient)
	{
		IngredientJS i = IngredientJS.of(ingredient);

		for (RecipeJS r : list)
		{
			if (r.hasInput(i))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasOutput(Object ingredient)
	{
		IngredientJS i = IngredientJS.of(ingredient);

		for (RecipeJS r : list)
		{
			if (r.hasOutput(i))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean replaceInput(Object ingredient, Object with)
	{
		IngredientJS i = IngredientJS.of(ingredient);
		IngredientJS o = IngredientJS.of(with);
		boolean changed = false;

		for (RecipeJS r : list)
		{
			if (r.replaceInput(i, o))
			{
				changed = true;

				if (recipeChanged != null)
				{
					recipeChanged.accept(r);
				}
			}
		}

		return changed;
	}

	@Override
	public boolean replaceOutput(Object ingredient, Object with)
	{
		IngredientJS i = IngredientJS.of(ingredient);
		ItemStackJS o = ItemStackJS.of(with);
		boolean changed = false;

		for (RecipeJS r : list)
		{
			if (r.replaceOutput(i, o))
			{
				changed = true;

				if (recipeChanged != null)
				{
					recipeChanged.accept(r);
				}
			}
		}

		return changed;
	}

	@Override
	public void setGroup(String group)
	{
		for (RecipeJS r : list)
		{
			if (!r.getGroup().equals(group))
			{
				r.setGroup(group);

				if (recipeChanged != null)
				{
					recipeChanged.accept(r);
				}
			}
		}
	}

	public RecipeCollection filter(Predicate<RecipeJS> r)
	{
		List<RecipeJS> list1 = new ArrayList<>();

		for (RecipeJS recipe : list)
		{
			if (r.test(recipe))
			{
				list1.add(recipe);
			}
		}

		if (list1.size() == list.size())
		{
			return this;
		}

		RecipeCollection collection = new RecipeCollection(list1);
		collection.recipeChanged = recipeChanged;
		return collection;
	}
}