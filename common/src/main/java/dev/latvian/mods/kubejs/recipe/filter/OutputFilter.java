package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;

/**
 * @author LatvianModder
 */
public class OutputFilter implements RecipeFilter {
	private final ReplacementMatch match;

	public OutputFilter(ReplacementMatch match) {
		this.match = match;
	}

	@Override
	public boolean test(RecipeKJS r) {
		return r.hasOutput(match);
	}

	@Override
	public String toString() {
		return "OutputFilter{" + match + '}';
	}
}
