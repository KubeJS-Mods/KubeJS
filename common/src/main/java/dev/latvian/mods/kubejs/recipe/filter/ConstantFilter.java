package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeKJS;

public record ConstantFilter(boolean filter) implements RecipeFilter {
	public static final ConstantFilter TRUE = new ConstantFilter(true);
	public static final ConstantFilter FALSE = new ConstantFilter(false);

	@Override
	public boolean test(RecipeKJS r) {
		return filter;
	}

	@Override
	public String toString() {
		return filter ? "*" : "-";
	}
}
