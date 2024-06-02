package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.AfterRecipesLoadedKubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

// FIXME
public record KubeJSReloadListener(ReloadableServerResources resources) implements ResourceManagerReloadListener {
	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		var recipeManager = resources.getRecipeManager();

		if (ServerEvents.RECIPES_AFTER_LOADED.hasListeners()) {
			ServerEvents.RECIPES_AFTER_LOADED.post(ScriptType.SERVER, new AfterRecipesLoadedKubeEvent(recipeManager.byType, recipeManager.byName));
		}
	}
}
