package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeKJS;

import java.util.ArrayList;
import java.util.List;

public class OrFilter implements RecipeFilter {
	public final List<RecipeFilter> list = new ArrayList<>(2);

	@Override
	public boolean test(RecipeKJS r) {
		for (var p : list) {
			if (p.test(r)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return "OrFilter[" + list + ']';
	}
}
