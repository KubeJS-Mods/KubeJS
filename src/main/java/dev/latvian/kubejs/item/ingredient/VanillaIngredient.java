package dev.latvian.kubejs.item.ingredient;

import net.minecraft.item.crafting.Ingredient;

/**
 * @author LatvianModder
 */
public class VanillaIngredient extends Ingredient
{
	public final IngredientJS ingredientJS;

	public VanillaIngredient(IngredientJS i)
	{
		super(0);
		ingredientJS = i;
	}
}