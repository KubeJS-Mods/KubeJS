package dev.latvian.mods.kubejs.recipe.viewer.server;

import net.neoforged.bus.api.Event;
import org.jspecify.annotations.Nullable;

public class RemoteRecipeViewerDataUpdatedEvent extends Event {
	public final @Nullable RecipeViewerData data;

	public RemoteRecipeViewerDataUpdatedEvent(@Nullable RecipeViewerData data) {
		this.data = data;
	}
}
