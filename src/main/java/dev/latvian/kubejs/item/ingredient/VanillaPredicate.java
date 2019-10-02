package dev.latvian.kubejs.item.ingredient;

import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class VanillaPredicate implements Predicate<ItemStack>
{
	private final IngredientJS ingredient;

	public VanillaPredicate(IngredientJS i)
	{
		ingredient = i;
	}

	@Override
	public boolean test(ItemStack stack)
	{
		return ingredient.testVanilla(stack);
	}
}