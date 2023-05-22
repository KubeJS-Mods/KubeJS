package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.MutableBoolean;

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

	public boolean hasInput(RecipeKJS recipe, ReplacementMatch match) {
		return key.component().hasInput(recipe, value, match);
	}

	public boolean replaceInput(RecipeKJS recipe, ReplacementMatch match, InputReplacement with) {
		var changed = new MutableBoolean(false);
		value = key.component().replaceInput(recipe, value, match, with, changed);
		this.changed |= changed.value;
		return changed.value;
	}

	public boolean hasOutput(RecipeKJS recipe, ReplacementMatch match) {
		return key.component().hasOutput(recipe, value, match);
	}

	public boolean replaceOutput(RecipeKJS recipe, ReplacementMatch match, OutputReplacement with) {
		var changed = new MutableBoolean(false);
		value = key.component().replaceOutput(recipe, value, match, with, changed);
		this.changed |= changed.value;
		return changed.value;
	}
}
