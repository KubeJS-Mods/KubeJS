package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.core.RecipeKJS;

@FunctionalInterface
public interface InputReplacementTransformer {
	Object transform(RecipeKJS recipe, ReplacementMatch match, InputReplacement original, InputReplacement with);

	record Replacement(InputReplacement with, InputReplacementTransformer transformer) implements InputReplacement {
		@Override
		public String toString() {
			return with + " [transformed]";
		}
	}
}
