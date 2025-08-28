package dev.latvian.mods.kubejs.recipe.filter;

public record ConstantFilter(boolean filter) implements RecipeFilter {
	public static final ConstantFilter TRUE = new ConstantFilter(true);
	public static final ConstantFilter FALSE = new ConstantFilter(false);

	@Override
	public boolean test(RecipeMatchContext cx) {
		return filter;
	}

	@Override
	public String toString() {
		return filter ? "*" : "-";
	}
}
