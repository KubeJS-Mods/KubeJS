package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.Context;

public interface RegisterSubtypeKubeEvent extends KubeEvent {
	RecipeViewerEntryType getType();

	void register(Context cx, Object filter, SubtypeInterpreter interpreter);
}
