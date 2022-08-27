package dev.latvian.mods.kubejs.recipe.filter;

import net.minecraft.world.item.crafting.Ingredient;

/**
 * @author LatvianModder
 */
public class InputFilter implements RecipeFilter {
	private final Ingredient in;
	private final boolean exact;

	public InputFilter(Ingredient i, boolean e) {
		in = i;
		exact = e;
	}

	@Override
	public boolean test(FilteredRecipe r) {
		return r.hasInput(in, exact);
	}

	@Override
	public String toString() {
		return "InputFilter{" +
				"in=" + in +
				", exact=" + exact +
				'}';
	}
}
