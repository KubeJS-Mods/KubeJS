package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import dev.latvian.mods.kubejs.recipe.InputItemTransformer;
import dev.latvian.mods.kubejs.recipe.OutputItemTransformer;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
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
		var s = KubeJSRegistries.recipeSerializers().getId(((Recipe<?>) this).getSerializer());
		return RecipeNamespace.getAll().get(s.getNamespace()).get(s.getPath()).schema;
	}

	default String kjs$getMod() {
		return kjs$getOrCreateId().getNamespace();
	}

	default ResourceLocation kjs$getType() {
		return KubeJSRegistries.recipeSerializers().getId(((Recipe<?>) this).getSerializer());
	}

	default boolean kjs$hasInput(IngredientMatch match) {
		for (var in : ((Recipe<?>) this).getIngredients()) {
			if (match.contains(in)) {
				return true;
			}
		}

		return false;
	}

	default boolean kjs$replaceInput(IngredientMatch match, InputItem with, InputItemTransformer transformer) {
		return false;
	}

	default boolean kjs$hasOutput(IngredientMatch match) {
		return match.contains(((Recipe<?>) this).getResultItem());
	}

	default boolean kjs$replaceOutput(IngredientMatch match, OutputItem with, OutputItemTransformer transformer) {
		return false;
	}
}
