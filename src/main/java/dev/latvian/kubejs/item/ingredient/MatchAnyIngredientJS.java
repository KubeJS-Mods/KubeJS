package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CompoundIngredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class MatchAnyIngredientJS implements IngredientJS
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
	public Set<ItemStackJS> stacks()
	{
		Set<ItemStackJS> set = new LinkedHashSet<>();

		for (IngredientJS ingredient : ingredients)
		{
			set.addAll(ingredient.stacks());
		}

		return set;
	}

	@Override
	public Ingredient createVanillaIngredient()
	{
		List<Ingredient> list = new ArrayList<>(ingredients.size());

		for (IngredientJS ingredient : ingredients)
		{
			list.add(ingredient.createVanillaIngredient());
		}

		return new CompoundIngredientWrapper(list);
	}

	@Override
	public boolean isEmpty()
	{
		return ingredients.isEmpty();
	}

	private static class CompoundIngredientWrapper extends CompoundIngredient
	{
		private CompoundIngredientWrapper(Collection<Ingredient> children)
		{
			super(children);
		}
	}
}
