package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeKJS;

/**
 * @author MaxNeedsSnacks
 */
public record NotFilter(RecipeFilter original) implements RecipeFilter {
	@Override
	public boolean test(RecipeKJS r) {
		return !original.test(r);
	}

	@Override
	public String toString() {
		return "NotFilter{" + original + '}';
	}
}
