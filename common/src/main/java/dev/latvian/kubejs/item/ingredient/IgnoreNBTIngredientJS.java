package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.ItemStackJS;
import java.util.Set;
import net.minecraft.world.item.ItemStack;

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
		return item.areItemsEqual(stack);
	}

	@Override
	public boolean testVanilla(ItemStack stack)
	{
		return item.getItem() == stack.getItem();
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		return item.getStacks();
	}
}