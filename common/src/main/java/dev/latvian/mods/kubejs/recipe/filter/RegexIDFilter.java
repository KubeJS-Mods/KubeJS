package dev.latvian.mods.kubejs.recipe.filter;

import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class RegexIDFilter implements RecipeFilter {
	private final Pattern pattern;

	public RegexIDFilter(Pattern i) {
		pattern = i;
	}

	@Override
	public boolean test(FilteredRecipe r) {
		return pattern.matcher(r.getOrCreateId().toString()).find();
	}

	@Override
	public String toString() {
		return "RegexIDFilter{" +
				"pattern=" + pattern +
				'}';
	}
}
