package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.OutputItem;

@FunctionalInterface
public interface OutputItemTransformer {
	OutputItemTransformer DEFAULT = (recipe, match, original, with) -> with.copyWithProperties(original);

	// Won't be needed with Ichor
	Object transformJS(RecipeJS recipe, IngredientMatch match, OutputItem original, OutputItem with);

	default OutputItem transform(RecipeJS recipe, IngredientMatch match, OutputItem original, OutputItem with) {
		return OutputItem.of(transformJS(recipe, match, original, with));
	}
}
