package dev.latvian.mods.kubejs.recipe.schema.function;

import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

import java.util.List;

@FunctionalInterface
public interface ResolvedRecipeSchemaFunction {
	default List<RecipeComponent<?>> arguments() {
		return List.of();
	}

	void execute(RecipeScriptContext cx, List<Object> args);
}
