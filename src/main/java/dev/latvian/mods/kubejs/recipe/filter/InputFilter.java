package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;

public record InputFilter(ReplacementMatchInfo match) implements RecipeFilter {
	@Override
	public boolean test(RecipeMatchContext cx) {
		return cx.recipe().hasInput(cx, match);
	}

	@Override
	public String toString() {
		return "InputFilter{" + match + '}';
	}
}
