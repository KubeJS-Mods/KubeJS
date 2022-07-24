package dev.latvian.mods.kubejs.item.ingredient;

import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public final class NotIngredientJS implements IngredientJS {
	private final IngredientJS ingredientJS;

	public NotIngredientJS(IngredientJS i) {
		ingredientJS = i;
	}

	@Override
	public boolean test(ItemStack stack) {
		return !ingredientJS.test(stack);
	}

	@Override
	public IngredientJS not() {
		return ingredientJS;
	}

	@Override
	public IngredientJS copy() {
		return new NotIngredientJS(ingredientJS.copy());
	}

	@Override
	public boolean isInvalidRecipeIngredient() {
		return ingredientJS.isInvalidRecipeIngredient();
	}

	@Override
	public String toString() {
		return "!" + ingredientJS;
	}
}