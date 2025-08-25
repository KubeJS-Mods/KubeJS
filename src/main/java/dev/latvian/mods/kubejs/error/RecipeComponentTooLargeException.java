package dev.latvian.mods.kubejs.error;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

public class RecipeComponentTooLargeException extends KubeRuntimeException {
	public final RecipeComponent<?> component;
	public final int max;

	public RecipeComponentTooLargeException(RecipeComponent<?> component, int size, int max) {
		super("Component '" + component.toString() + "' is too large (" + size + ")! Max size is " + max);
		this.component = component;
		this.max = max;

		customData("invalid_component", component);
		customData("max_size", max);
	}
}
