package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.rhino.Context;

public record ConstantFilter(boolean filter) implements RecipeFilter {
	public static final ConstantFilter TRUE = new ConstantFilter(true);
	public static final ConstantFilter FALSE = new ConstantFilter(false);

	@Override
	public boolean test(Context cx, RecipeLikeKJS r) {
		return filter;
	}

	@Override
	public String toString() {
		return filter ? "*" : "-";
	}
}
