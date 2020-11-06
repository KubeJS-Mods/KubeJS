package dev.latvian.kubejs.item.ingredient;

import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;

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