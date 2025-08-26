package dev.latvian.mods.kubejs.recipe.schema.postprocessing;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.component.ValidationContext;

public interface RecipePostProcessor {
	RecipePostProcessorType<?> type();

	void process(ValidationContext ctx, KubeRecipe recipe);
}
