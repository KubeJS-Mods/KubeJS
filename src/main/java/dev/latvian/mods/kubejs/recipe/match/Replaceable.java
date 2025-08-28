package dev.latvian.mods.kubejs.recipe.match;

import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;

public interface Replaceable {
	default Object replaceThisWith(RecipeScriptContext cx, Object with) {
		return this;
	}
}
