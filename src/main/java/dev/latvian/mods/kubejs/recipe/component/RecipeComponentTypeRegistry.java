package dev.latvian.mods.kubejs.recipe.component;

@FunctionalInterface
public interface RecipeComponentTypeRegistry {
	void register(RecipeComponentType<?> type);
}
