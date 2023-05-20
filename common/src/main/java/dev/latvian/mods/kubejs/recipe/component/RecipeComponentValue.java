package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import dev.latvian.mods.kubejs.recipe.InputItemTransformer;
import dev.latvian.mods.kubejs.recipe.OutputItemTransformer;
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

	public boolean hasInput(IngredientMatch match) {
		return key.component().hasInput(value, match);
	}

	public boolean replaceInput(IngredientMatch match, InputItem with, InputItemTransformer transformer) {
		var v0 = value;
		value = key.component().replaceInput(value, match, with, transformer);
		return value != v0;
	}

	public boolean hasOutput(IngredientMatch match) {
		return key.component().hasOutput(value, match);
	}

	public boolean replaceOutput(IngredientMatch match, OutputItem with, OutputItemTransformer transformer) {
		var v0 = value;
		value = key.component().replaceOutput(value, match, with, transformer);
		return value != v0;
	}
}
