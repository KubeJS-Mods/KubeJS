package dev.latvian.mods.kubejs.recipe.viewer;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface SubtypeInterpreter {
	@Nullable Object apply(Object entry);
}
