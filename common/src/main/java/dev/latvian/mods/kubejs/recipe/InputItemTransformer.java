package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.InputItem;

@FunctionalInterface
public interface InputItemTransformer {
	InputItemTransformer DEFAULT = (recipe, match, original, with) -> with.copyWithProperties(original);

	// Won't be needed with Ichor
	Object transformJS(RecipeJS recipe, IngredientMatch match, InputItem original, InputItem with);

	default InputItem transform(RecipeJS recipe, IngredientMatch match, InputItem original, InputItem with) {
		return InputItem.of(transformJS(recipe, match, original, with));
	}
}
