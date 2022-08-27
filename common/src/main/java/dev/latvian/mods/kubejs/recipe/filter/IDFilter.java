package dev.latvian.mods.kubejs.recipe.filter;

import net.minecraft.resources.ResourceLocation;

/**
 * @author LatvianModder
 */
public class IDFilter implements RecipeFilter {
	private final ResourceLocation id;

	public IDFilter(ResourceLocation i) {
		id = i;
	}

	@Override
	public boolean test(FilteredRecipe r) {
		return r.getOrCreateId().equals(id);
	}

	@Override
	public String toString() {
		return "IDFilter{" +
				"id=" + id +
				'}';
	}
}
