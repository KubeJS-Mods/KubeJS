package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import net.minecraft.core.HolderLookup;

import java.util.ArrayList;
import java.util.List;

public class AndFilter implements RecipeFilter {
	public final List<RecipeFilter> list = new ArrayList<>(2);

	@Override
	public boolean test(HolderLookup.Provider registries, RecipeLikeKJS r) {
		for (var p : list) {
			if (!p.test(registries, r)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return "AndFilter[" + list + ']';
	}
}
