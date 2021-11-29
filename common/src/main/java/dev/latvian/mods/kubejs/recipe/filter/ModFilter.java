package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.recipe.RecipeJS;

/**
 * @author LatvianModder
 */
public class ModFilter implements RecipeFilter {
	private final String mod;

	public ModFilter(String m) {
		mod = m;
	}

	@Override
	public boolean test(RecipeJS r) {
		return r.getMod().equals(mod);
	}

	@Override
	public String toString() {
		return "ModFilter{" +
				"mod='" + mod + '\'' +
				'}';
	}
}
