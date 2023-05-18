package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.RecipeKey;

public class RecipeComponentValue<T> {
	public static final RecipeComponentValue<?>[] EMPTY_ARRAY = new RecipeComponentValue[0];

	public final RecipeKey<T> key;
	public T value;
	public boolean changed;

	public RecipeComponentValue(RecipeKey<T> key) {
		this.key = key;
		this.value = null;
		this.changed = false;
	}
}
