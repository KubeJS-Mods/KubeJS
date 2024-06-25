package dev.latvian.mods.kubejs.recipe.match;

import dev.latvian.mods.rhino.Context;

public interface Replaceable {
	default Object replaceThisWith(Context cx, Object with) {
		return this;
	}
}
