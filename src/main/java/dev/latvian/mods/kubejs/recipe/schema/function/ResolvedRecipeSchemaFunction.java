package dev.latvian.mods.kubejs.recipe.schema.function;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

@FunctionalInterface
public interface ResolvedRecipeSchemaFunction {
	default TypeInfo[] getArgTypes() {
		return TypeInfo.EMPTY_ARRAY;
	}

	void execute(Context cx, KubeRecipe recipe, Object[] args);
}
