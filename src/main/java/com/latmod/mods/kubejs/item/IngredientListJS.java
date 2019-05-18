package com.latmod.mods.kubejs.item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class IngredientListJS implements IIngredientJS
{
	public final List<IIngredientJS> ingredients = new ArrayList<>();

	@Override
	public boolean test(ItemStackJS stack)
	{
		for (IIngredientJS ingredient : ingredients)
		{
			if (ingredient.test(stack))
			{
				return true;
			}
		}

		return false;
	}
}
