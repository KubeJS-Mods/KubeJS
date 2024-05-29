package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import net.minecraft.core.HolderLookup;

public record NotFilter(RecipeFilter original) implements RecipeFilter {
	@Override
	public boolean test(HolderLookup.Provider registries, RecipeLikeKJS r) {
		return !original.test(registries, r);
	}

	@Override
	public String toString() {
		return "NotFilter{" + original + '}';
	}
}
