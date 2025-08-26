package dev.latvian.mods.kubejs.recipe.schema.postprocessing;

@FunctionalInterface
public interface RecipePostProcessorTypeRegistry {
	void register(RecipePostProcessorType<?> type);
}
