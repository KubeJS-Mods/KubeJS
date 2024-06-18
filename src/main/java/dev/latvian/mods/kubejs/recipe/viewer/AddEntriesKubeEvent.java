package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.Context;

public interface AddEntriesKubeEvent extends KubeEvent {
	void add(Context cx, Object[] items);
}
