package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.core.RecipeKJS;

@FunctionalInterface
public interface OutputReplacementTransformer {
	Object transform(RecipeKJS recipe, ReplacementMatch match, OutputReplacement original, OutputReplacement with);

	record Replacement(OutputReplacement with, OutputReplacementTransformer transformer) implements OutputReplacement {
		@Override
		public String toString() {
			return with + " [transformed]";
		}

		@Override
		public Object replaceOutput(RecipeJS recipe, ReplacementMatch match, OutputReplacement original) {
			return transformer.transform(recipe, match, original, with);
		}
	}
}
