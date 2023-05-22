package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;

/**
 * @author LatvianModder
 */
public class InputFilter implements RecipeFilter {
	private final ReplacementMatch match;

	public InputFilter(ReplacementMatch match) {
		this.match = match;
	}

	@Override
	public boolean test(RecipeKJS r) {
		return r.hasInput(match);
	}

	@Override
	public String toString() {
		return "InputFilter{" + match + '}';
	}
}
