package dev.latvian.mods.kubejs.recipe.schema.function;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;

public class RecipeSchemaJSFunction extends BaseFunction {
	public final KubeRecipe recipe;
	public final ResolvedRecipeSchemaFunction func;
	public final TypeInfo[] argTypes;
	public boolean convertArgs;

	public RecipeSchemaJSFunction(KubeRecipe recipe, ResolvedRecipeSchemaFunction func) {
		this.recipe = recipe;
		this.func = func;
		this.argTypes = func.getArgTypes();
		this.convertArgs = false;

		for (var type : argTypes) {
			if (type != TypeInfo.NONE) {
				convertArgs = true;
				break;
			}
		}
	}

	@Override
	public KubeRecipe call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		var args1 = args;

		if (convertArgs) {
			args1 = new Object[args.length];

			for (int i = 0; i < args.length; i++) {
				args1[i] = cx.jsToJava(args[i], argTypes[i]);
			}
		}

		func.execute(cx, recipe, args1);
		return recipe;
	}
}
