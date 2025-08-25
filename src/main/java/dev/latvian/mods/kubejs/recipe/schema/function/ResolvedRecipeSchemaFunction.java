package dev.latvian.mods.kubejs.recipe.schema.function;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.rhino.Context;

import java.util.List;

@FunctionalInterface
public interface ResolvedRecipeSchemaFunction {
	default List<RecipeComponent<?>> arguments() {
		return List.of();
	}

	void execute(Context cx, KubeRecipe recipe, List<Object> args);
}
