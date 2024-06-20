package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.recipe.viewer.server.RecipeViewerData;
import dev.latvian.mods.kubejs.recipe.viewer.server.RecipeViewerDataUpdatedEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "jei")
public class JEIIntegrationEventHandler {
	public static RecipeViewerData remote = null;

	@SubscribeEvent
	public static void loadRemote(RecipeViewerDataUpdatedEvent event) {
		remote = event.data;
	}
}
