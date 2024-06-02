package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.rhino.Context;

public class GroupFilter implements RecipeFilter {
	private final String group;

	public GroupFilter(String g) {
		group = g;
	}

	@Override
	public boolean test(Context cx, RecipeLikeKJS r) {
		return r.kjs$getGroup().equals(group);
	}

	@Override
	public String toString() {
		return "GroupFilter{" +
			"group='" + group + '\'' +
			'}';
	}
}
