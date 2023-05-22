package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.core.RecipeKJS;

public interface InputReplacement {
	default <T> T replaceInput(RecipeKJS recipe, ReplacementMatch match, T previousValue) {
		return (T) this;
	}
}
