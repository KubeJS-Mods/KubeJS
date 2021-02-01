package dev.latvian.kubejs.recipe.filter;

import dev.latvian.kubejs.recipe.RecipeJS;

/**
 * @author LatvianModder
 */
public class GroupFilter implements RecipeFilter
{
	private final String group;

	public GroupFilter(String g)
	{
		group = g;
	}

	@Override
	public boolean test(RecipeJS r)
	{
		return r.getGroup().equals(group);
	}

	@Override
	public String toString()
	{
		return "GroupFilter{" +
				"group='" + group + '\'' +
				'}';
	}
}
