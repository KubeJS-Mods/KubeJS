package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeKJS;

/**
 * @author LatvianModder
 */
public class GroupFilter implements RecipeFilter {
	private final String group;

	public GroupFilter(String g) {
		group = g;
	}

	@Override
	public boolean test(RecipeKJS r) {
		return r.kjs$getGroup().equals(group);
	}

	@Override
	public String toString() {
		return "GroupFilter{" +
				"group='" + group + '\'' +
				'}';
	}
}
