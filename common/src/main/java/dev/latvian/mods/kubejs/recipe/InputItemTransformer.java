package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.item.InputItem;

@FunctionalInterface
public interface InputItemTransformer {
	Object transform(RecipeKJS recipe, ReplacementMatch match, InputItem original, InputItem with);

	record Replacement(InputItem with, InputItemTransformer transformer) implements InputReplacement {
		@Override
		public <T> T replaceInput(RecipeKJS recipe, ReplacementMatch match, T previousValue) {
			return (T) InputItem.of(transformer.transform(recipe, match, (InputItem) previousValue, with));
		}

		@Override
		public String toString() {
			return with + " [transformed]";
		}
	}
}
