package dev.latvian.mods.kubejs.recipe.schema.function;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.ArrayList;
import java.util.List;

public class RecipeSchemaJSFunction extends BaseFunction {
	public final KubeRecipe recipe;
	public final TypeInfo[] argTypes;
	public final ResolvedRecipeSchemaFunction func;

	public RecipeSchemaJSFunction(KubeRecipe recipe, TypeInfo[] argTypes, ResolvedRecipeSchemaFunction func) {
		this.recipe = recipe;
		this.argTypes = argTypes;
		this.func = func;
	}

	@Override
	public KubeRecipe call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		if (argTypes.length == 0) {
			func.execute(cx, recipe, List.of());
			return recipe;
		}

		var argList = new ArrayList<>(argTypes.length);

		for (int i = 0; i < args.length; i++) {
			argList.add(cx.jsToJava(args[i], argTypes[i]));
		}

		func.execute(cx, recipe, argList);
		return recipe;
	}
}
