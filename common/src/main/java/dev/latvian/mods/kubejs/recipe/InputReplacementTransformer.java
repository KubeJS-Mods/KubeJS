package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.core.RecipeLikeKJS;

@FunctionalInterface
public interface InputReplacementTransformer {
	Object transform(RecipeLikeKJS recipe, ReplacementMatch match, InputReplacement original, InputReplacement with);

	record Replacement(InputReplacement with, InputReplacementTransformer transformer) implements InputReplacement {
		@Override
		public String toString() {
			return with + " [transformed]";
		}
	}
}
