package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.rhino.Context;

public record NotFilter(RecipeFilter original) implements RecipeFilter {
	@Override
	public boolean test(Context cx, RecipeLikeKJS r) {
		return !original.test(cx, r);
	}

	@Override
	public String toString() {
		return "NotFilter{" + original + '}';
	}
}
