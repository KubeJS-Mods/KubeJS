package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.rhino.Context;

public class OutputFilter implements RecipeFilter {
	private final ReplacementMatch match;

	public OutputFilter(ReplacementMatch match) {
		this.match = match;
	}

	@Override
	public boolean test(Context cx, RecipeLikeKJS r) {
		return r.hasOutput(cx, match);
	}

	@Override
	public String toString() {
		return "OutputFilter{" + match + '}';
	}
}
