package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.core.RecipeKJS;

public interface OutputReplacement {
	default <T> T replaceOutput(RecipeKJS recipe, ReplacementMatch match, T original) {
		return (T) this;
	}
}
