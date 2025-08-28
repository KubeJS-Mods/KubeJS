package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.script.WithScriptContext;
import dev.latvian.mods.kubejs.util.ErrorStack;
import dev.latvian.mods.rhino.Context;

public interface RecipeScriptContext extends WithScriptContext, RecipeValidationContext, KubeRecipeContext, RecipeMatchContext {
	record Impl(Context cx, KubeRecipe recipe, ErrorStack errors) implements RecipeScriptContext {
		public Impl(Context cx, KubeRecipe recipe) {
			this(cx, recipe, ErrorStack.NONE);
		}
	}
}
