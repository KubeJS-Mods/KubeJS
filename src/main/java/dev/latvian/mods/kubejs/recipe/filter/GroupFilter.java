package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import net.minecraft.core.HolderLookup;

public class GroupFilter implements RecipeFilter {
	private final String group;

	public GroupFilter(String g) {
		group = g;
	}

	@Override
	public boolean test(HolderLookup.Provider registries, RecipeLikeKJS r) {
		return r.kjs$getGroup().equals(group);
	}

	@Override
	public String toString() {
		return "GroupFilter{" +
			"group='" + group + '\'' +
			'}';
	}
}
