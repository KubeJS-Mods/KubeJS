package dev.latvian.mods.kubejs.integration;

import dev.latvian.mods.kubejs.event.EventGroup;

// TODO: Replacement for JEIEvents and REIEvents and possibly other mod integrations
public interface RecipeViewerEvents {
	EventGroup GROUP = EventGroup.of("RecipeViewerEvents");

	static void register() {
		GROUP.register();
	}
}
