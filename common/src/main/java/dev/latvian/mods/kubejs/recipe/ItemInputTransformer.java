package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.ingredient.IngredientStack;
import net.minecraft.world.item.crafting.Ingredient;

@FunctionalInterface
public interface ItemInputTransformer {
	ItemInputTransformer DEFAULT = (recipe, match, original, with) -> {
		if (original instanceof IngredientStack stack) {
			return (Ingredient) with.kjs$withCount(stack.getCount());
		}

		return with;
	};

	Ingredient transform(RecipeJS recipe, IngredientMatch match, Ingredient original, Ingredient with);
}
