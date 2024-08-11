package dev.latvian.mods.kubejs.error;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;

public class InvalidRecipeComponentException extends KubeRuntimeException {
	public final RecipeComponentValue<?> componentValueHolder;

	public InvalidRecipeComponentException(RecipeComponentValue<?> h, Throwable cause) {
		super("Invalid component '" + h.key.name + "' (" + h.key.component + ")", cause);
		this.componentValueHolder = h;
	}
}
