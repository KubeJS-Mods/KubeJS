package dev.latvian.mods.kubejs.error;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

public class EmptyRecipeComponentException extends InvalidRecipeComponentValueException {
	public EmptyRecipeComponentException(RecipeComponent<?> component, Object value) {
		super("Component '" + component.toString() + "' is not allowed to be empty!", component, value);
	}
}
