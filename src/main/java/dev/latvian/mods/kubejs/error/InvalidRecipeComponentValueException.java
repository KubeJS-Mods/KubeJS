package dev.latvian.mods.kubejs.error;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

public class InvalidRecipeComponentValueException extends RecipeComponentException {
	public final RecipeComponent<?> component;
	public final Object value;

	public InvalidRecipeComponentValueException(String message, RecipeComponent<?> component, Object value) {
		super(message, null, component, null, value);
		this.component = component;
		this.value = value;

		customData("component", component);
		customData("value", value);
	}

	public InvalidRecipeComponentValueException(RecipeComponent<?> component, Object value) {
		this("Component '" + component + "' has an invalid value!", component, value);
	}
}
