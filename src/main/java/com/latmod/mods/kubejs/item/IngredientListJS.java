package com.latmod.mods.kubejs.item;

import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CompoundIngredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	@Override
	public Set<ItemStackJS> getStacks()
	{
		Set<ItemStackJS> set = new HashSet<>();

		for (IIngredientJS ingredient : ingredients)
		{
			set.addAll(ingredient.getStacks());
		}

		return set;
	}

	@Override
	public Ingredient createVanillaIngredient()
	{
		List<Ingredient> list = new ArrayList<>(ingredients.size());

		for (IIngredientJS ingredient : ingredients)
		{
			list.add(ingredient.createVanillaIngredient());
		}

		return new CompoundIngredientWrapper(list);
	}

	private static class CompoundIngredientWrapper extends CompoundIngredient
	{
		private CompoundIngredientWrapper(Collection<Ingredient> children)
		{
			super(children);
		}
	}
}
