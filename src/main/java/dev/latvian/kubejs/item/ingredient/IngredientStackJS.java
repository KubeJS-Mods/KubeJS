package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.item.ItemStack;

import java.util.Set;

/**
 * @author LatvianModder
 */
public class IngredientStackJS implements IngredientJS
{
	private final IngredientJS ingredient;
	private final int countOverride;

	public IngredientStackJS(IngredientJS i, int a)
	{
		ingredient = i;
		countOverride = a;
	}

	public IngredientJS getIngredient()
	{
		return ingredient;
	}

	@Override
	public int getCount()
	{
		return countOverride;
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return ingredient.test(stack);
	}

	@Override
	public boolean test(ItemStack stack)
	{
		return ingredient.test(stack);
	}

	@Override
	public boolean isEmpty()
	{
		return ingredient.isEmpty();
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		return ingredient.getStacks();
	}

	@Override
	public IngredientJS not()
	{
		return new IngredientStackJS(ingredient.not(), countOverride);
	}

	@Override
	public ItemStackJS getFirst()
	{
		return ingredient.getFirst().count(getCount());
	}

	@Override
	public String toString()
	{
		return getCount() == 1 ? ingredient.toString() : (getCount() + "x " + ingredient);
	}
}