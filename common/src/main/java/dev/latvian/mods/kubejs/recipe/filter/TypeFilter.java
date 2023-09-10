package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;

public class TypeFilter implements RecipeFilter {
	private final ResourceLocation type;

	public TypeFilter(ResourceLocation t) {
		type = t;

		if (RecipeJS.itemErrors && !RegistryInfo.RECIPE_SERIALIZER.hasValue(type)) {
			throw new RecipeExceptionJS("Type '" + type + "' doesn't exist!").error();
		}
	}

	@Override
	public boolean test(RecipeKJS r) {
		return r.kjs$getType().equals(type);
	}

	@Override
	public String toString() {
		return "TypeFilter{" + type + '}';
	}
}
