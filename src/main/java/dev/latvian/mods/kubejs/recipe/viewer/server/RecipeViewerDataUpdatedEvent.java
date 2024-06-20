package dev.latvian.mods.kubejs.recipe.viewer.server;

import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

public class RecipeViewerDataUpdatedEvent extends Event {
	public final RecipeViewerData data;

	public RecipeViewerDataUpdatedEvent(@Nullable RecipeViewerData data) {
		this.data = data;
	}
}
