package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

@RemapPrefixForJS("kjs$")
public interface RecipeKJS {
	default String kjs$getGroup() {
		return ((Recipe<?>) this).getGroup();
	}

	default void kjs$setGroup(String group) {
	}

	default ResourceLocation kjs$getOrCreateId() {
		return ((Recipe<?>) this).getId();
	}

	default RecipeSchema kjs$getSchema() {
		var s = RegistryInfo.RECIPE_SERIALIZER.getId(((Recipe<?>) this).getSerializer());
		return RecipeNamespace.getAll().get(s.getNamespace()).get(s.getPath()).schema;
	}

	default String kjs$getMod() {
		return kjs$getOrCreateId().getNamespace();
	}

	default ResourceLocation kjs$getType() {
		return RegistryInfo.RECIPE_SERIALIZER.getId(((Recipe<?>) this).getSerializer());
	}

	default boolean hasInput(ReplacementMatch match) {
		if (match instanceof ItemMatch m) {
			for (var in : ((Recipe<?>) this).getIngredients()) {
				if (m.contains(in)) {
					return true;
				}
			}
		}

		return false;
	}

	default boolean replaceInput(ReplacementMatch match, InputReplacement with) {
		return false;
	}

	default boolean hasOutput(ReplacementMatch match) {
		return match instanceof ItemMatch m && m.contains(((Recipe<?>) this).getResultItem(UtilsJS.staticRegistryAccess));
	}

	default boolean replaceOutput(ReplacementMatch match, OutputReplacement with) {
		return false;
	}
}
