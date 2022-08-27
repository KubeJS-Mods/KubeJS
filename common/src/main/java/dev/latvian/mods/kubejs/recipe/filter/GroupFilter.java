package dev.latvian.mods.kubejs.recipe.filter;

/**
 * @author LatvianModder
 */
public class GroupFilter implements RecipeFilter {
	private final String group;

	public GroupFilter(String g) {
		group = g;
	}

	@Override
	public boolean test(FilteredRecipe r) {
		return r.getGroup().equals(group);
	}

	@Override
	public String toString() {
		return "GroupFilter{" +
				"group='" + group + '\'' +
				'}';
	}
}
