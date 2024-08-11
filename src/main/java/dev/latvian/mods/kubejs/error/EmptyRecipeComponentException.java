package dev.latvian.mods.kubejs.error;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

public class EmptyRecipeComponentException extends KubeRuntimeException {
	public final RecipeComponent<?> component;

	public EmptyRecipeComponentException(RecipeComponent<?> component) {
		super("Component '" + component + "' is not allowed to be empty!");
		this.component = component;
	}
}
