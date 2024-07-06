package dev.latvian.mods.kubejs.error;

import dev.latvian.mods.kubejs.recipe.RecipeKey;

import java.util.Collection;

public class MissingComponentException extends KubeRuntimeException {
	public final RecipeKey<?> key;
	public final Collection<RecipeKey<?>> valid;

	public MissingComponentException(String keyName, RecipeKey<?> key, Collection<RecipeKey<?>> valid) {
		super("Recipe component key '" + keyName + "' not found! Valid keys: " + valid);
		this.key = key;
		this.valid = valid;
	}
}
