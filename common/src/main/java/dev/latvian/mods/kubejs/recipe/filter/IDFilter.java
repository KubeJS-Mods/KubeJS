package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import net.minecraft.resources.ResourceLocation;

public class IDFilter implements RecipeFilter {
	public final ResourceLocation id;

	public IDFilter(ResourceLocation i) {
		id = i;
	}

	@Override
	public boolean test(RecipeLikeKJS r) {
		return r.kjs$getOrCreateId().equals(id);
	}

	@Override
	public String toString() {
		return "IDFilter{" +
			"id=" + id +
			'}';
	}
}
