package dev.latvian.mods.kubejs.recipe.schema.postprocessing;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;

public interface RecipePostProcessor {
	Codec<RecipePostProcessor> CODEC = RecipePostProcessorType.CODEC.dispatch("type", RecipePostProcessor::type, RecipePostProcessorType::mapCodec);

	RecipePostProcessorType<?> type();

	void process(RecipeValidationContext ctx, KubeRecipe recipe);
}
