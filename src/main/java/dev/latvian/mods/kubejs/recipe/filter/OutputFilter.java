package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;

public class OutputFilter implements RecipeFilter {
	private final ReplacementMatchInfo match;

	public OutputFilter(ReplacementMatchInfo match) {
		this.match = match;
	}

	@Override
	public boolean test(RecipeMatchContext cx) {
		return cx.recipe().hasOutput(cx, match);
	}

	@Override
	public String toString() {
		return "OutputFilter{" + match + '}';
	}
}
