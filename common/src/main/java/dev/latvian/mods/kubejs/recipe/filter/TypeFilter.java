package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import net.minecraft.resources.ResourceLocation;

/**
 * @author LatvianModder
 */
public class TypeFilter implements RecipeFilter {
	private final String type;

	public TypeFilter(String t) {
		type = t;

		if (RecipeJS.itemErrors && !KubeJSRegistries.recipeSerializers().contains(new ResourceLocation(type))) {
			throw new RecipeExceptionJS("Type '" + type + "' doesn't exist!").error();
		}
	}

	@Override
	public boolean test(FilteredRecipe r) {
		return r.getType().equals(type);
	}

	@Override
	public String toString() {
		return "TypeFilter{" +
				"type='" + type + '\'' +
				'}';
	}
}
