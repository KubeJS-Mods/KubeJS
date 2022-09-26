package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeKJS;

/**
 * @author MaxNeedsSnacks
 */
public class NotFilter implements RecipeFilter {
	public final RecipeFilter original;

	public NotFilter(RecipeFilter original) {
		this.original = original;
	}

	@Override
	public boolean test(RecipeKJS r) {
		return !original.test(r);
	}

	@Override
	public String toString() {
		return "NotFilter{" + original + '}';
	}
}
