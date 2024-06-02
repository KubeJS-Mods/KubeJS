package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.rhino.Context;

public record InputFilter(ReplacementMatch match) implements RecipeFilter {
	@Override
	public boolean test(Context cx, RecipeLikeKJS r) {
		return r.hasInput(cx, match);
	}

	@Override
	public String toString() {
		return "InputFilter{" + match + '}';
	}
}
