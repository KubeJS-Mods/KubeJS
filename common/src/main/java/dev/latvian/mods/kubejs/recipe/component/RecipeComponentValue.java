package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;

public final class RecipeComponentValue<T> extends BaseFunction implements WrappedJS {
	public static final RecipeComponentValue<?>[] EMPTY_ARRAY = new RecipeComponentValue[0];

	public final RecipeJS recipe;
	public final RecipeKey<T> key;
	public T value;
	public boolean write;

	public RecipeComponentValue(RecipeJS recipe, RecipeKey<T> key) {
		this.recipe = recipe;
		this.key = key;
		this.value = null;
		this.write = false;
	}

	public boolean isInput(ReplacementMatch match) {
		return value != null && key.component.isInput(recipe, value, match);
	}

	public boolean replaceInput(ReplacementMatch match, InputReplacement with) {
		var newValue = value == null ? null : key.component.replaceInput(recipe, value, match, with);

		if (value != newValue) {
			value = newValue;
			write = true;
			return true;
		}

		return false;
	}

	public boolean isOutput(ReplacementMatch match) {
		return value != null && key.component.isOutput(recipe, value, match);
	}

	public boolean replaceOutput(ReplacementMatch match, OutputReplacement with) {
		var newValue = value == null ? null : key.component.replaceOutput(recipe, value, match, with);

		if (value != newValue) {
			value = newValue;
			write = true;
			return true;
		}

		return false;
	}

	@Override
	public RecipeJS call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		return recipe.setValue(key, key.component.read(recipe, args[0]));
	}

	@Override
	public String toString() {
		return key.name;
	}

	public String checkEmpty() {
		return key.allowEmpty ? "" : value != null ? key.component.checkEmpty(key, value) : key.optional() ? "" : ("Value of '" + key.name + "' can't be null!");
	}
}
