package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Set;

/**
 * @author LatvianModder
 */
public final class IgnoreNBTIngredientJS implements IngredientJS
{
	private final ItemStackJS item;

	public IgnoreNBTIngredientJS(ItemStackJS i)
	{
		item = i;
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		int d = item.getData();
		return (d == OreDictionary.WILDCARD_VALUE || d == stack.getData()) && item.areItemsEqual(stack);
	}

	@Override
	public boolean test(ItemStack stack)
	{
		int d = item.getData();
		return (d == OreDictionary.WILDCARD_VALUE || d == stack.getMetadata()) && item.getItem() == stack.getItem();
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		return item.getStacks();
	}
}