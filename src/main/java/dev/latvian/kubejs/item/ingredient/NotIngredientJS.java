package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
public final class NotIngredientJS implements IngredientJS
{
	private final IngredientJS ingredientJS;

	public NotIngredientJS(IngredientJS i)
	{
		ingredientJS = i;
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return !ingredientJS.test(stack);
	}

	@Override
	public boolean testVanilla(ItemStack stack)
	{
		return !ingredientJS.testVanilla(stack);
	}

	@Override
	public IngredientJS not()
	{
		return ingredientJS;
	}
}