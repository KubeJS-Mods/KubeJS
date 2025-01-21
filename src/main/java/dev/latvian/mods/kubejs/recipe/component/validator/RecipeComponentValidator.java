package dev.latvian.mods.kubejs.recipe.component.validator;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

public interface RecipeComponentValidator {
	Codec<RecipeComponentValidator> CODEC = RecipeComponentValidatorType.CODEC.dispatch("type", RecipeComponentValidator::getType, RecipeComponentValidatorType::codec);

	RecipeComponentValidatorType<?> getType();

	<T> boolean isValid(RecipeComponent<T> component, T value);

	default <T> RuntimeException createError(RecipeComponent<T> component, T value) {
		return new InvalidRecipeComponentValueException(component, value, this);
	}
}
