package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValueFunction;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaFunction;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.HashMap;
import java.util.Map;

public class RecipeFunction extends NativeJavaObject {
	public final KubeRecipe recipe;
	public final Map<String, BaseFunction> builderFunctions;

	private static boolean isValidIdentifier(char[] name) {
		if (name.length == 0 || !Character.isJavaIdentifierStart(name[0])) {
			return false;
		}

		for (int i = 1; i < name.length; i++) {
			if (!Character.isJavaIdentifierPart(name[i])) {
				return false;
			}
		}

		return true;
	}

	public RecipeFunction(Context cx, Scriptable scope, TypeInfo staticType, KubeRecipe recipe) {
		super(scope, recipe, staticType, cx);
		this.recipe = recipe;
		this.builderFunctions = new HashMap<>();

		for (var value : recipe.getRecipeComponentValues()) {
			var names = value.key.functionNames == null ? value.key.names : value.key.functionNames;

			if (!names.isEmpty()) {
				var func = new RecipeComponentValueFunction(recipe, value);

				for (var name : names) {
					if (isValidIdentifier(name.toCharArray())) {
						builderFunctions.put(name, func);
					}
				}
			}
		}

		for (var entry : recipe.type.schemaType.schema.functions.entrySet()) {
			if (isValidIdentifier(entry.getKey().toCharArray())) {
				builderFunctions.put(entry.getKey(), new RecipeSchemaFunction.JSFunction(recipe, entry.getValue()));
			}
		}
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
