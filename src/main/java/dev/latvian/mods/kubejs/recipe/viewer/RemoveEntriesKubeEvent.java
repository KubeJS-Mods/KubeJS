package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.Context;

public interface RemoveEntriesKubeEvent extends KubeEvent {
	void remove(Context cx, Object filter);

	void removeDirectly(Context cx, Object filter);

	void removeAll();

	Object[] getAllEntryValues();
}
