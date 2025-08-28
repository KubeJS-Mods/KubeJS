package dev.latvian.mods.kubejs.recipe.schema.postprocessing;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;

public interface RecipePostProcessor {
	RecipePostProcessorType<?> type();

	void process(RecipeValidationContext ctx, KubeRecipe recipe);
}
