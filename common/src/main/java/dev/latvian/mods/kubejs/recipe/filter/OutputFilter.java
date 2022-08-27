package dev.latvian.mods.kubejs.recipe.filter;

import net.minecraft.world.item.crafting.Ingredient;

/**
 * @author LatvianModder
 */
public class OutputFilter implements RecipeFilter {
	private final Ingredient out;
	private final boolean exact;

	public OutputFilter(Ingredient o, boolean e) {
		out = o;
		exact = e;
	}

	@Override
	public boolean test(FilteredRecipe r) {
		return r.hasOutput(out, exact);
	}

	@Override
	public String toString() {
		return "OutputFilter{" +
				"out=" + out +
				", exact=" + exact +
				'}';
	}
}
