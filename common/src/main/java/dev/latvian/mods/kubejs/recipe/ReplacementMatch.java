package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;

public interface ReplacementMatch {
	static ReplacementMatch of(Object o) {
		// FIXME: Add exact: true/false support
		// TODO: Add support for other types of replacements
		return new IngredientMatch(IngredientJS.of(o), false);
	}
}
