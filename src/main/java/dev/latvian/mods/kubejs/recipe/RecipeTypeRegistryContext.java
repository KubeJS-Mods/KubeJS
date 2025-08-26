package dev.latvian.mods.kubejs.recipe;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaStorage;
import dev.latvian.mods.kubejs.recipe.schema.postprocessing.RecipePostProcessor;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;

public record RecipeTypeRegistryContext(
	RegistryAccessContainer registries,
	RecipeSchemaStorage storage
) {
	public Codec<RecipeComponent<?>> recipeComponentCodec() {
		return storage.recipeComponentCodec;
	}

	public Codec<RecipePostProcessor> recipePostProcessorCodec() {
		return storage.recipePostProcessorCodec;
	}
}