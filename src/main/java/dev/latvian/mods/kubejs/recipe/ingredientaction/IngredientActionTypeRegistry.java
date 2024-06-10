package dev.latvian.mods.kubejs.recipe.ingredientaction;

@FunctionalInterface
public interface IngredientActionTypeRegistry {
	void register(IngredientActionType type);
}
