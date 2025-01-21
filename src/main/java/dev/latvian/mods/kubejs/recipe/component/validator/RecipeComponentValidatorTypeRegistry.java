package dev.latvian.mods.kubejs.recipe.component.validator;

@FunctionalInterface
public interface RecipeComponentValidatorTypeRegistry {
	void register(RecipeComponentValidatorType<?> type);
}
