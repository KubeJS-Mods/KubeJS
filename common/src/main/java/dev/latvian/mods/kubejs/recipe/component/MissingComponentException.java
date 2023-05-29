package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.RecipeKey;

public class MissingComponentException extends RuntimeException {
	public final RecipeKey<?> key;

	public MissingComponentException(RecipeKey<?> key) {
		super("Required recipe component key '" + key.name() + "' is missing!'");
		this.key = key;
	}
}
