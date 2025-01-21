package dev.latvian.mods.kubejs.error;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.validator.RecipeComponentValidator;

public class InvalidRecipeComponentValueException extends KubeRuntimeException {
	public final RecipeComponent<?> component;
	public final Object value;
	public final RecipeComponentValidator validator;

	public InvalidRecipeComponentValueException(RecipeComponent<?> component, Object value, RecipeComponentValidator validator) {
		super("Component '" + component + "' has an invalid value!");
		this.component = component;
		this.value = value;
		this.validator = validator;

		customData("invalid_component", component.toString());
		customData("invalid_value", value.toString());
		customData("validator", validator.toString());
	}
}
