package dev.latvian.mods.kubejs.recipe.viewer.server;

import net.neoforged.bus.api.Event;

public class RecipeViewerDataSyncedEvent extends Event {
	public final RecipeViewerData data;

	public RecipeViewerDataSyncedEvent(RecipeViewerData data) {
		this.data = data;
	}
}
