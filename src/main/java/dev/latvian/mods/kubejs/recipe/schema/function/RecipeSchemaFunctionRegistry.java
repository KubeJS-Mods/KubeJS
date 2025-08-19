package dev.latvian.mods.kubejs.recipe.schema.function;

@FunctionalInterface
public interface RecipeSchemaFunctionRegistry {
	void register(RecipeSchemaFunctionType<?> type);
}
