package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeLikeKJS;

public record NotFilter(RecipeFilter original) implements RecipeFilter {
	@Override
	public boolean test(RecipeLikeKJS r) {
		return !original.test(r);
	}

	@Override
	public String toString() {
		return "NotFilter{" + original + '}';
	}
}
