package dev.latvian.kubejs.recipe.ingredientaction;

import dev.latvian.kubejs.item.ingredient.IngredientJS;
import net.minecraft.world.item.ItemStack;

public class IngredientActionFilter {
	public int filterIndex = -1;
	public IngredientJS filterIngredient = null;

	public void copyFrom(IngredientActionFilter filter) {
		filterIndex = filter.filterIndex;
		filterIngredient = filter.filterIngredient;
	}

	public boolean checkFilter(int index, ItemStack stack) {
		return (filterIndex == -1 || filterIndex == index) && (filterIngredient == null || filterIngredient.testVanilla(stack));
	}
}
