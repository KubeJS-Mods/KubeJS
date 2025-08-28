package dev.latvian.mods.kubejs.recipe.filter;

public class ModFilter implements RecipeFilter {
	private final String mod;

	public ModFilter(String m) {
		mod = m;
	}

	@Override
	public boolean test(RecipeMatchContext cx) {
		return cx.recipe().kjs$getMod().equals(mod);
	}

	@Override
	public String toString() {
		return "ModFilter{" +
			"mod='" + mod + '\'' +
			'}';
	}
}
