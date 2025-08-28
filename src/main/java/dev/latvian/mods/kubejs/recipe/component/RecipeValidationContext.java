package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.KubeRecipeContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.util.ErrorStack;

public interface RecipeValidationContext extends KubeRecipeContext, RecipeMatchContext {
	record Impl(KubeRecipe recipe, ErrorStack errors) implements RecipeValidationContext {
	}

	ErrorStack errors();
}
