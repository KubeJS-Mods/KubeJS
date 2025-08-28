package dev.latvian.mods.kubejs.recipe.filter;

public class GroupFilter implements RecipeFilter {
	private final String group;

	public GroupFilter(String g) {
		group = g;
	}

	@Override
	public boolean test(RecipeMatchContext cx) {
		return cx.recipe().kjs$getGroup().equals(group);
	}

	@Override
	public String toString() {
		return "GroupFilter{" +
			"group='" + group + '\'' +
			'}';
	}
}
