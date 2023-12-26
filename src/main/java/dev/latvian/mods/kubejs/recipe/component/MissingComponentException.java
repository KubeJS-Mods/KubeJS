package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;

import java.util.Collection;

public class MissingComponentException extends RecipeExceptionJS {
	public final RecipeKey<?> key;
	public final Collection<RecipeKey<?>> valid;

	public MissingComponentException(String keyName, RecipeKey<?> key, Collection<RecipeKey<?>> valid) {
		super("Recipe component key '" + keyName + "' not found! Valid keys: " + valid);
		this.key = key;
		this.valid = valid;
	}
}
