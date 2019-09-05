package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.item.ItemStack;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class MatchAllIngredientJS implements IngredientJS
{
	public static MatchAllIngredientJS INSTANCE = new MatchAllIngredientJS();

	private MatchAllIngredientJS()
	{
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return !stack.isEmpty();
	}

	@Override
	public boolean test(ItemStack stack)
	{
		return !stack.isEmpty();
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		return new LinkedHashSet<>(ItemStackJS.list());
	}

	@Override
	public IngredientJS not()
	{
		return EmptyItemStackJS.INSTANCE;
	}
}