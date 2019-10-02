package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.item.ItemStack;

import java.util.LinkedHashSet;
import java.util.List;
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
	public boolean testVanilla(ItemStack stack)
	{
		return !stack.isEmpty();
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		Set<ItemStackJS> set = new LinkedHashSet<>();

		for (ItemStackJS stack : ItemStackJS.getList())
		{
			set.add(stack.getCopy());
		}

		return set;
	}

	@Override
	public ItemStackJS getFirst()
	{
		List<ItemStackJS> list = ItemStackJS.getList();
		return list.isEmpty() ? EmptyItemStackJS.INSTANCE : list.get(0).getCopy();
	}

	@Override
	public IngredientJS not()
	{
		return EmptyItemStackJS.INSTANCE;
	}
}