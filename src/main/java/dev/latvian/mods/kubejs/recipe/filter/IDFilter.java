package dev.latvian.mods.kubejs.recipe.filter;

import net.minecraft.resources.Identifier;

public class IDFilter implements RecipeFilter {
	public final Identifier id;

	public IDFilter(Identifier i) {
		id = i;
	}

	@Override
	public boolean test(RecipeMatchContext cx) {
		return cx.recipe().kjs$getOrCreateId().equals(id);
	}

	@Override
	public String toString() {
		return "IDFilter{" +
			"id=" + id +
			'}';
	}
}
