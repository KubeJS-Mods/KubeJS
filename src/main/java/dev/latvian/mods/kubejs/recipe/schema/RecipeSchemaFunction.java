package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.List;
import java.util.Map;

public interface RecipeSchemaFunction {
	class JSFunction extends BaseFunction {
		public final KubeRecipe recipe;
		public final RecipeSchemaFunction func;
		public final TypeInfo[] argTypes;
		public boolean convertArgs;

		public JSFunction(KubeRecipe recipe, RecipeSchemaFunction func) {
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

	default TypeInfo[] getArgTypes() {
		return TypeInfo.EMPTY_ARRAY;
	}

	void execute(Context cx, KubeRecipe recipe, Object[] args);

	record Bundle(List<RecipeSchemaFunction> functions) implements RecipeSchemaFunction {
		@Override
		public void execute(Context cx, KubeRecipe recipe, Object[] args) {
			for (var function : functions) {
				function.execute(cx, recipe, args);
			}
		}
	}

	record SetFunction<T>(RecipeKey<T> key, T to) implements RecipeSchemaFunction {
		@Override
		public void execute(Context cx, KubeRecipe recipe, Object[] args) {
			recipe.setValue(key, to);
		}
	}

	record SetManyFunction(Map<RecipeKey<?>, Object> map) implements RecipeSchemaFunction {
		@Override
		public void execute(Context cx, KubeRecipe recipe, Object[] args) {
			for (var entry : map.entrySet()) {
				recipe.setValue(entry.getKey(), Cast.to(entry.getValue()));
			}
		}
	}
}
