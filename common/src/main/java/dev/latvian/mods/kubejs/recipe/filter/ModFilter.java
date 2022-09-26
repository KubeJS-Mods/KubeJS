package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeKJS;

/**
 * @author LatvianModder
 */
public class ModFilter implements RecipeFilter {
	private final String mod;

	public ModFilter(String m) {
		mod = m;
	}

	@Override
	public boolean test(RecipeKJS r) {
		return r.kjs$getMod().equals(mod);
	}

	@Override
	public String toString() {
		return "ModFilter{" +
				"mod='" + mod + '\'' +
				'}';
	}
}
