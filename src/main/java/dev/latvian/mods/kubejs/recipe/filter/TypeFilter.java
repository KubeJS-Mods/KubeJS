package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.rhino.Context;
import net.minecraft.resources.ResourceLocation;

public class TypeFilter implements RecipeFilter {
	private final ResourceLocation type;

	public TypeFilter(ResourceLocation t) {
		type = t;
	}

	@Override
	public boolean test(Context cx, RecipeLikeKJS r) {
		return r.kjs$getType().equals(type);
	}

	@Override
	public String toString() {
		return "TypeFilter{" + type + '}';
	}
}
