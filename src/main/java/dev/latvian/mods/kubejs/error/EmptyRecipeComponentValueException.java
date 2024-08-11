package dev.latvian.mods.kubejs.error;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

public class EmptyRecipeComponentValueException extends KubeRuntimeException {
	public final RecipeComponent<?> component;

	public EmptyRecipeComponentValueException(RecipeComponent<?> component) {
		super("Component '" + component + "' is not allowed to contain empty values!");
		this.component = component;
	}
}
