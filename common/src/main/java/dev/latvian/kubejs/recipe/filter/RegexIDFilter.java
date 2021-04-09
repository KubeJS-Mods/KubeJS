package dev.latvian.kubejs.recipe.filter;

import dev.latvian.kubejs.recipe.RecipeJS;

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
	public boolean test(RecipeJS r) {
		return pattern.matcher(r.getOrCreateId().toString()).find();
	}

	@Override
	public String toString() {
		return "RegexIDFilter{" +
				"pattern=" + pattern +
				'}';
	}
}
