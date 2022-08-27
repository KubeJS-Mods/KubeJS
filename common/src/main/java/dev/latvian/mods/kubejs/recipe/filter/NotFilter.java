package dev.latvian.mods.kubejs.recipe.filter;

/**
 * @author MaxNeedsSnacks
 */
public class NotFilter implements RecipeFilter {
	public final RecipeFilter original;

	public NotFilter(RecipeFilter original) {
		this.original = original;
	}

	@Override
	public boolean test(FilteredRecipe r) {
		return !original.test(r);
	}

	@Override
	public String toString() {
		return "NotFilter{" + original + '}';
	}
}
