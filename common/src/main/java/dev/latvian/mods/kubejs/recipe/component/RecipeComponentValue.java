package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;

public class RecipeComponentValue<T> {
	public static final RecipeComponentValue<?>[] EMPTY_ARRAY = new RecipeComponentValue[0];

	public final RecipeJS recipe;
	public final RecipeKey<T> key;
	public T value;
	public boolean changed;

	public RecipeComponentValue(RecipeJS recipe, RecipeKey<T> key) {
		this.recipe = recipe;
		this.key = key;
		this.value = null;
		this.changed = false;
	}

	public boolean isInput(ReplacementMatch match) {
		return key.component().isInput(recipe, value, match);
	}

	public boolean replaceInput(ReplacementMatch match, InputReplacement with) {
		var newValue = key.component().replaceInput(recipe, value, match, with);

		if (value != newValue) {
			value = newValue;
			changed = true;
			return true;
		}

		return false;
	}

	public boolean isOutput(ReplacementMatch match) {
		return key.component().isOutput(recipe, value, match);
	}

	public boolean replaceOutput(ReplacementMatch match, OutputReplacement with) {
		var newValue = key.component().replaceOutput(recipe, value, match, with);

		if (value != newValue) {
			value = newValue;
			changed = true;
			return true;
		}

		return false;
	}
}
