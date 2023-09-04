package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Wrapper;

public class RecipeComponentValueFunction extends BaseFunction {
	public final RecipeJS recipe;
	public final RecipeComponentValue<?> componentValue;

	public RecipeComponentValueFunction(RecipeJS recipe, RecipeComponentValue<?> componentValue) {
		this.recipe = recipe;
		this.componentValue = componentValue;
	}

	@Override
	public RecipeJS call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		return recipe.setValue(componentValue.key, UtilsJS.cast(componentValue.key.component.read(recipe, Wrapper.unwrapped(args[0]))));
	}
}
