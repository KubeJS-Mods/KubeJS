package dev.latvian.mods.kubejs.error;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;

import javax.annotation.Nullable;

public class RecipeComponentException extends KubeRuntimeException {
	public final RecipeComponent<?> component;
	public final @Nullable RecipeKey<?> key;
	public final Object value;

	public RecipeComponentException(String msg, Throwable cause, RecipeComponentValue<?> value) {
		this(msg, cause, value.key.component, value.key, value.value);
	}

	public RecipeComponentException(String msg, Throwable cause, RecipeComponent<?> component, @Nullable RecipeKey<?> key, Object value) {
		super(msg, cause);

		this.component = component;
		this.key = key;
		this.value = value;

		customData("component", component);
		customData("value", value);

		if (key != null) {
			customData("key", key.name);
		}
	}
}
