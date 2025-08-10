package dev.latvian.mods.kubejs.error;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

public class InvalidRecipeComponentValueException extends KubeRuntimeException {
	public final RecipeComponent<?> component;
	public final Object value;

	public InvalidRecipeComponentValueException(RecipeComponent<?> component, Object value) {
		super("Component '" + component + "' has an invalid value!");
		this.component = component;
		this.value = value;

		customData("invalid_component", component.toString());
		customData("invalid_value", value.toString());
	}
}
