package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.item.OutputItem;

@FunctionalInterface
public interface OutputItemTransformer {
	Object transform(RecipeKJS recipe, ReplacementMatch match, OutputItem original, OutputItem with);

	record Replacement(OutputItem with, OutputItemTransformer transformer) implements OutputReplacement {
		@Override
		public <T> T replaceOutput(RecipeKJS recipe, ReplacementMatch match, T original) {
			return (T) OutputItem.of(transformer.transform(recipe, match, (OutputItem) original, with));
		}

		@Override
		public String toString() {
			return with + " [transformed]";
		}
	}
}
