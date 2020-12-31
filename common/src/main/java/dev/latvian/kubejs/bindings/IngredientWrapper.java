package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAllIngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAnyIngredientJS;

import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class IngredientWrapper
{
	public IngredientJS getNone()
	{
		return EmptyItemStackJS.INSTANCE;
	}

	public IngredientJS getAll()
	{
		return MatchAllIngredientJS.INSTANCE;
	}

	public IngredientJS of(Object object)
	{
		return IngredientJS.of(object);
	}

	public IngredientJS of(Object object, int count)
	{
		return of(object).withCount(Math.max(1, count));
	}

	public IngredientJS custom(Predicate<ItemStackJS> predicate)
	{
		return predicate::test;
	}

	public IngredientJS matchAny(Object objects)
	{
		MatchAnyIngredientJS ingredient = new MatchAnyIngredientJS();
		ingredient.addAll(objects);
		return ingredient;
	}
}