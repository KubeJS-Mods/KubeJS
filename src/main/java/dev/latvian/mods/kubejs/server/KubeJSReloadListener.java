package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.AfterRecipesLoadedKubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class KubeJSReloadListener implements ResourceManagerReloadListener {
	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		var recipeManager = UtilsJS.staticResources == null ? null : UtilsJS.staticResources.getRecipeManager();

		if (recipeManager != null && ServerEvents.RECIPES_AFTER_LOADED.hasListeners()) {
			ServerEvents.RECIPES_AFTER_LOADED.post(ScriptType.SERVER, new AfterRecipesLoadedKubeEvent(recipeManager.byType, recipeManager.byName));
		}
	}
}
