package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValueFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;

import java.util.HashMap;
import java.util.Map;

public class RecipeFunction extends NativeJavaObject {
	public final RecipeJS recipe;
	public final Map<String, RecipeComponentValueFunction> builderFunctions;

	public RecipeFunction(Context cx, Scriptable scope, Class<?> staticType, RecipeJS recipe) {
		super(scope, recipe, staticType, cx);
		this.recipe = recipe;
		var map = recipe.getAllValueMap();
		this.builderFunctions = new HashMap<>(map.size());

		for (var entry : map.entrySet()) {
			var key = entry.getKey();
			var value = entry.getValue();
			if (!value.key.noBuilders) {
				builderFunctions.put(key, new RecipeComponentValueFunction(recipe, value));
			}
		}
	}

	@Override
	public Object get(Context cx, String name, Scriptable start) {
		if (recipe instanceof ErroredRecipeJS errored) {
			return errored.dummyFunction;
		}

		var s = super.get(cx, name, start);

		if (s == Scriptable.NOT_FOUND) {
			var r = builderFunctions.get(name);

			if (r != null) {
				return r;
			}
		}

		return s;
	}
}
