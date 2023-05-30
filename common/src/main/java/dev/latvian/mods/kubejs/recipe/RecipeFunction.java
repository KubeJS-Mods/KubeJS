package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;

import java.util.Map;

public class RecipeFunction extends NativeJavaObject {
	public final RecipeJS recipe;
	public final Map<String, RecipeComponentValue<?>> builderFunctions;

	public RecipeFunction(Context cx, Scriptable scope, Class<?> staticType, RecipeJS recipe) {
		super(scope, recipe, staticType, cx);
		this.recipe = recipe;
		this.builderFunctions = recipe.getAllValueMap();
	}

	@Override
	public Object get(Context cx, String name, Scriptable start) {
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
