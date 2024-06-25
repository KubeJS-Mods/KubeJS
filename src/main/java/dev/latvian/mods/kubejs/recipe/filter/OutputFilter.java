package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.rhino.Context;

public class OutputFilter implements RecipeFilter {
	private final ReplacementMatchInfo match;

	public OutputFilter(ReplacementMatchInfo match) {
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
